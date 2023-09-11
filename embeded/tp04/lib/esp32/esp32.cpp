// Copyright 2022 Haute école d'ingénierie et d'architecture de Fribourg
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

/****************************************************************************
 * @file esp32.cpp
 * @author Patrick Bovey <patrick.bovey@hefr.ch>
 *
 * @brief ESP32 Click Boards (mikro-e) interface
 *
 * @date 2022-03-02
 * @version 0.1.0
 ***************************************************************************/

#include "esp32.hpp"

#include <mbed.h>

#include "mbed_trace.h"

#if defined(MBED_CONF_MBED_TRACE_ENABLE)
#define TRACE_GROUP "ESP32"
#endif  // MBED_CONF_MBED_TRACE_ENABLE

Esp32::Esp32(ClickBoard::Id id)
    : cb_(id),
      cs_(cb_.CS, kCsOff_),
      espSerial_(cb_.TX, cb_.RX, 115200),
      espParser_(&espSerial_, "\r\n", 256, kATDefaultTimeout_),
      parsePublisherCallbackSem_(0)
{
    espSerial_.set_flow_control(SerialBase::Flow::Disabled, NC, NC);
    espSerial_.set_format(8, BufferedSerial::None, 1);
    cs_ = kCsOn_;
}

// public

void Esp32::setATDebug(bool debug) { espParser_.debug_on(debug); }

bool Esp32::getBleName(char* name)
{
    smutex_.lock();
    espParser_.send("AT+BLENAME?");
    if (espParser_.recv("+BLENAME:%s\r\nOK", name)) {
        smutex_.unlock();
        return true;
    } else {
        smutex_.unlock();
        return false;
    }
}

bool Esp32::getBleVersion(char* version)
{
    smutex_.lock();
    espParser_.send("AT+GMR");
    if (espParser_.recv("%s\r\nOK", version)) {
        smutex_.unlock();
        return true;
    } else {
        smutex_.unlock();
        return false;
    }
}

bool Esp32::getBleAddress(char* address)
{
    smutex_.lock();
    espParser_.send("AT+BLEADDR?");
    if (espParser_.recv("+BLEADDR:%s\r\nOK", address)) {
        smutex_.unlock();
        return true;
    } else {
        smutex_.unlock();
        return false;
    }
}

bool Esp32::startBlePublisher(const char* name, bool autoAdvertising)
{
    if (initBlePublisher(name))
        if (startBleAdvertising(autoAdvertising)) return true;
    return false;
}

bool Esp32::initBlePublisher(const char* name)
{
    smutex_.lock();
    if (espParser_.recv("ready") && espParser_.send("AT+BLEINIT=2") && espParser_.recv("OK") &&
        espParser_.send("AT+BLENAME=\"%s\"", name) && espParser_.recv("OK") &&
        espParser_.send("AT+BLEGATTSSRVCRE") && espParser_.recv("OK") &&
        espParser_.send("AT+BLEGATTSSRVSTART") && espParser_.recv("OK")) {
        espParser_.oob("+READ:", callback(this, &Esp32::callbackSubscriberRead));
        espParser_.oob("+WRITE:", callback(this, &Esp32::callbackSubscriberWrite));
        espParser_.oob("+BLECONN:", callback(this, &Esp32::callbackSubscriberConnect));
        espParser_.oob("+BLEDISCONN:", callback(this, &Esp32::callbackSubscriberDisconnect));
        smutex_.unlock();
        parsePublisherCallback_.start(callback(this, &Esp32::parseSubscriberCallback));
        return true;
    } else {
        smutex_.unlock();
        return false;
    }
}

bool Esp32::getBleGATTService(char* service)
{
    smutex_.lock();
    espParser_.send("AT+BLEGATTSSRV?");
    if (espParser_.recv("+BLEGATTSSRV:%s\r\nOK", service)) {
        smutex_.unlock();
        return true;
    } else {
        smutex_.unlock();
        return false;
    }
}

bool Esp32::startBleAdvertising(bool autoAdvertising)
{
    autoAdvertising_ = autoAdvertising;
    smutex_.lock();
    espParser_.set_timeout(5000);
    if (espParser_.send("AT+BLEADVSTART") && espParser_.recv("OK")) {
        advertising_ = true;
        smutex_.unlock();
        parsePublisherCallbackSem_.release();
        return true;
    } else {
        advertising_ = false;
        smutex_.unlock();
        return false;
    }
}

bool Esp32::notifyPublisherData(char* data, int dataLenght)
{
    bool success = false;
    if (bleNotificationOn_) {
        smutex_.lock();
        // AT+BLEGATTSNTFY=<conn_index>,<srv_index>,<char_index>,<length>
        if (espParser_.send("AT+BLEGATTSNTFY=%d,%d,%d,%d",
                            bleConnIndex_,
                            bleSrvIndex_,
                            BLE_NOTIFICATION_CHAR,
                            dataLenght) &&
            espParser_.recv(">")) {
            for (int i = 0; i < dataLenght; i++) espParser_.putc(data[i]);
            espParser_.flush();
            if (espParser_.recv("OK")) {
                success = true;
            }
        }
        smutex_.unlock();
    }
    return success;
}

bool Esp32::indicatePublisherData(char* data, int dataLenght)
{
    bool success = false;
    if (bleIndicationOn_) {
        smutex_.lock();
        // AT+BLEGATTSIND=<conn_index>,<srv_index>,<char_index>,<length>
        if (espParser_.send("AT+BLEGATTSIND=%d,%d,%d,%d",
                            bleConnIndex_,
                            bleSrvIndex_,
                            BLE_INDICATION_CHAR,
                            dataLenght) &&
            espParser_.recv(">")) {
            for (int i = 0; i < dataLenght; i++) espParser_.putc(data[i]);
            espParser_.flush();
            if (espParser_.recv("OK")) {
                success = true;
            }
        }
        smutex_.unlock();
    }
    return success;
}

void Esp32::attachSubscriberRead(mbed::Callback<void()> cbFunc)
{
    callbackSubscriberRead_ = cbFunc;
}

void Esp32::attachSubscriberWrite(mbed::Callback<void()> cbFunc)
{
    callbackSubscriberWrite_ = cbFunc;
}

void Esp32::attachSubscriberConnect(mbed::Callback<void()> cbFunc)
{
    callbackSubscriberConnect_ = cbFunc;
}

void Esp32::attachSubscriberDisconnect(mbed::Callback<void()> cbFunc)
{
    callbackSubscriberDisconnect_ = cbFunc;
}

// private

bool Esp32::subscriberRead()
{
    uint8_t mac[6] = {0};
    smutex_.lock();
    espParser_.recv("%hhd,\"%hhx:%hhx:%hhx:%hhx:%hhx:%hhx\"",
                    &bleConnIndex_,
                    &mac[0],
                    &mac[1],
                    &mac[2],
                    &mac[3],
                    &mac[4],
                    &mac[5]);
    // tr_debug("conn_index=%d, mac[%x:%x:%x:%x:%x:%x]",
    //        bleConnIndex_, mac[0], mac[1], mac[2], mac[3], mac[4], mac[5]);
    smutex_.unlock();
    return true;
}

bool Esp32::subscriberWrite(Span<uint8_t>** spanData)
{
    uint8_t charIndex, descIndex = 0;
    uint16_t len;
    smutex_.lock();
    espParser_.recv("%hhd,%hhd,%hhd,", &bleConnIndex_, &bleSrvIndex_, &charIndex);
    // tr_debug("conn=%d srv=%d char=%d", bleConnIndex_, bleSrvIndex_, charIndex);

    char c = espParser_.getc();
    if (c != ',') {
        descIndex = c;
        espParser_.getc();  // to read ',' after desc_index.
        // tr_debug("desc=%d", descIndex);
    }

    espParser_.recv("%hd,", &len);
    // tr_debug("length=%d", len);

    uint8_t* data = (uint8_t*)malloc(len * sizeof(uint8_t));
    for (int i = 0; i < len; i++) {
        data[i] = espParser_.getc();
        // tr_debug("data[%x] = %x", i, data[i]);
    }
    smutex_.unlock();

    if (charIndex == BLE_WRITE_CHAR) {
        *spanData = new Span<uint8_t>(data, len * sizeof(uint8_t));
        // tr_debug("Span in");
        for (uint8_t i = 0; i < (*spanData)->size(); i++)
            tr_debug("data[%x] = %x", i, (*spanData)->data()[i]);
        return true;
    } else if ((descIndex == 49) && (charIndex == BLE_NOTIFICATION_CHAR)) {
        if (data[0] == 1) {
            tr_debug("Notification On");
            bleNotificationOn_ = true;
        } else {
            tr_debug("Notification Off");
            bleNotificationOn_ = false;
        }
    } else if ((descIndex == 49) && (charIndex == BLE_INDICATION_CHAR)) {
        if (data[0] == 2) {
            tr_debug("Indication On");
            bleIndicationOn_ = true;
        } else {
            tr_debug("Indication Off");
            bleIndicationOn_ = false;
        }
    }
    spanData = NULL;
    free(data);
    return false;
}

void Esp32::callbackSubscriberRead()
{
    // tr_debug("Read ...");
    subscriberRead();
    if (callbackSubscriberRead_ != NULL) this->callbackSubscriberRead_();
}

void Esp32::callbackSubscriberWrite()
{
    Span<uint8_t>* spanData;
    // tr_debug("Write ...");
    if (subscriberWrite(&spanData)) {
        // tr_debug("Span out");
        for (uint8_t i = 0; i < spanData->size(); i++)
            tr_debug("data[%x] = %x", i, spanData->data()[i]);
        free(spanData->data());
        delete (spanData);
    }
    if (callbackSubscriberWrite_ != NULL) this->callbackSubscriberWrite_();
}

void Esp32::callbackSubscriberConnect()
{
    subscriberConnected_ = true;
    if (callbackSubscriberConnect_ != NULL) this->callbackSubscriberConnect_();
}

void Esp32::callbackSubscriberDisconnect()
{
    subscriberConnected_ = false;
    advertising_         = false;
    bleNotificationOn_   = false;
    bleIndicationOn_     = false;

    if (autoAdvertising_)
        if (!startBleAdvertising(true)) tr_debug("Fail auto restard advertising!");
    if (callbackSubscriberDisconnect_ != NULL) this->callbackSubscriberDisconnect_();
}

bool Esp32::notifyBleCharacteristic()
{
    smutex_.lock();
    if (espParser_.send("AT+BLEGATTCWR=0,3,6,1,2") && espParser_.recv(">")) {
        espParser_.putc(1);  // activate notify !
        espParser_.putc(0);
        espParser_.flush();
        espParser_.recv("OK");
        smutex_.unlock();
        return true;
    } else {
        smutex_.unlock();
        return false;
    }
}

bool Esp32::publisherNotify()
{
    uint8_t char_index;
    uint8_t len;

    smutex_.lock();
    espParser_.recv("%hhd,%hhd,%hhd,%hhd,", &ble_conn_index_, &ble_srv_index_, &char_index, &len);

    uint8_t* data = (uint8_t*)malloc(len * sizeof(uint8_t));
    for (int i = 0; i < len; i++) {
        data[i] = espParser_.getc();
    }

    temperature = (data[1] << 8) + data[0];
    temperature = temperature / 100;
    humidity    = (data[3] << 8) + data[2];
    humidity    = humidity / 100;
    pressure    = (data[5] << 8) + data[4];

    smutex_.unlock();
    // free(data);
    return true;
}

void Esp32::parseSubscriberCallback()
{
    callbackPublisherActivated_ = true;
    while (true) {
        parsePublisherCallbackSem_.acquire();
        while (advertising_) {
            smutex_.lock();
            espParser_.set_timeout(kATShortTimeout_);
            espParser_.recv(" ");
            espParser_.set_timeout(kATDefaultTimeout_);
            smutex_.unlock();
            ThisThread::sleep_for(100ms);
        }
    }
}

bool Esp32::initBleSubscriber(const char*)
{
    return true;
    /* smutex_.lock();
     if (espParser_.send("AT+BLEINIT=1") && espParser_.recv("OK")) {
         espParser_.oob("+NOTIFY:", callback(this, &Esp32::callbackPublisherNotify));
         smutex_.unlock();
         parseClientCallBack_.start(callback(this, &Esp32::parsePublisherCallback));
         return true;
     } else {
         smutex_.unlock();
         return false;
     }*/
}

bool Esp32::wifiConnect(const char* ssid, const char* pwd)
{
    smutex_.lock();
    espParser_.send("AT+CWMODE=1");
    espParser_.recv("OK");
    espParser_.send("AT+CWJAP=\"%s\",\"%s\"", ssid, pwd);
    if (espParser_.recv("OK")) {
        smutex_.unlock();
        return true;
    } else {
        smutex_.unlock();
        tr_warning("Fail to connect from wifi");
        return false;
    }
}

bool Esp32::wifiDisconnect()
{
    smutex_.lock();
    espParser_.send("AT+CWQAP");
    if (espParser_.recv("OK")) {
        smutex_.unlock();
        return true;
    } else {
        smutex_.unlock();
        return false;
    }
}

bool Esp32::getIPAddress(uint8_t* ip, uint8_t* gw, uint8_t* msk)
{
    smutex_.lock();
    espParser_.send("AT+CIPSTA?");
    if (espParser_.recv(
            "+CIPSTA:ip:\"%hhd.%hhd.%hhd.%hhd\"\r\n", &ip[0], &ip[1], &ip[2], &ip[3]) &&
        espParser_.recv(
            "+CIPSTA:gateway:\"%hhd.%hhd.%hhd.%hhd\"\r\n", &gw[0], &gw[1], &gw[2], &gw[3]) &&
        espParser_.recv(
            "+CIPSTA:netmask:\"%hhd.%hhd.%hhd.%hhd\"\r\n", &msk[0], &msk[1], &msk[2], &msk[3]) &&
        espParser_.recv("OK")) {
        smutex_.unlock();
        return true;
    } else {
        smutex_.unlock();
        return false;
    }
}

size_t Esp32::getTime(char* ntpServer){
    smutex_.lock();
    espParser_.send("AT+CIPSTA?");
    //AT+CIPSNTPCFG?
    //AT+CIPSNTPTIME? --> GET TIME
    if (espParser_.recv("OK")) {
        smutex_.unlock();
        return true;
    } else {
        smutex_.unlock();
        return false;
    }
}