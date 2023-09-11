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
 * @file esp32.hpp
 * @author Patrick Bovey <patrick.bovey@hefr.ch>
 *
 * @brief ESP32 Click Boards (mikro-e) interface
 *
 * @date 2022-03-02
 * @version 0.1.0
 *
 *  ## GATT Profile
 *
 *   UUID	Access Propety	            Size(Byte)	Characteristic Index
 *   A002	Read	                    2	        -
 *   C300	Read	                    1	        1
 *   C301	Read	                    512	        2
 *   C302	Write	                    1	        3
 *   C303	Write Without Response	    3	        4
 *   C304	Write	                    2	        5
 *   C305	Notify	                    5	        6
 *   C306	Indicate	                5	        7
 *
 ***************************************************************************/

#ifndef ESP32_HPP_
#define ESP32_HPP_

#include <mbed.h>

#include "click_board.hpp"

#define BLE_NOTIFICATION_CHAR 6
#define BLE_INDICATION_CHAR 7
#define BLE_WRITE_CHAR 5

class Esp32 {
   public:
    Esp32(ClickBoard::Id id);
    ~Esp32(){};

    void setATDebug(bool debug);

    bool getBleName(char* name);
    bool getBleVersion(char* version);
    bool getBleAddress(char* version);

    bool startBlePublisher(bool autoAdvertising);
    bool initBlePublisher();
    bool getBleGATTService(char* service);
    bool startBleAdvertising(bool autoAdvertising);

    bool notifyPublisherData(char* data, int dataLenght);
    bool indicatePublisherData(char* data, int dataLenght);

    void attachSubscriberRead(mbed::Callback<void()> cbFunc);
    void attachSubscriberWrite(mbed::Callback<void()> cbFunc);
    void attachSubscriberConnect(mbed::Callback<void()> cbFunc);
    void attachSubscriberDisconnect(mbed::Callback<void()> cbFunc);

    bool initBleSubscriber();
    bool restoreBle();
    bool notifyBleCharacteristic();
    bool publisherNotify();

    bool wifiConnect(const char* ssid, const char* pwd);
    bool wifiDisconnect();
    bool getIPAddress(uint8_t* ip, uint8_t* gw, uint8_t* msk);

    bool mqttPublish(const char* topic, const char* data, uint8_t qos, uint8_t retain);
    bool mqttConnectBroker(
        const char* host, int port, const char* id, const char* user, const char* pwd);

   private:
    static constexpr int kATDefaultTimeout_    = 5000;
    static constexpr int kATShortTimeout_      = 5;
    static constexpr auto kParseCallbackDelay_ = 100ms;
    static constexpr int kUartBaudRate_        = 115200;
    static constexpr char kAtEol_[]            = "\r\n";
    static constexpr int kCsOff_               = 0;
    static constexpr int kCsOn_                = 1;

    uint8_t ble_conn_index_ = 0;
    uint8_t ble_srv_index_  = 0;
    float temperature       = 0;
    float humidity          = 0;
    float pressure          = 0;
    ClickBoard cb_;
    DigitalOut cs_;
    BufferedSerial espSerial_;
    ATCmdParser espParser_;

    bool subscriberConnected_        = false;
    bool advertising_                = false;
    bool autoAdvertising_            = false;
    bool callbackPublisherActivated_ = false;

    Mutex smutex_;  // protect serial interface
    Thread parsePublisherCallback_;
    Thread parseClientCallBack_;
    Semaphore parsePublisherCallbackSem_;

    uint8_t bleConnIndex_ = 0;
    uint8_t bleSrvIndex_  = 0;

    bool bleNotificationOn_ = false;
    bool bleIndicationOn_   = false;

    mbed::Callback<void()> callbackSubscriberRead_       = NULL;
    mbed::Callback<void()> callbackSubscriberWrite_      = NULL;
    mbed::Callback<void()> callbackSubscriberConnect_    = NULL;
    mbed::Callback<void()> callbackSubscriberDisconnect_ = NULL;
    mbed::Callback<void()> callbackPublisherNotify_      = NULL;

    bool subscriberRead();
    bool subscriberWrite(Span<uint8_t>** spanData);

    void callbackSubscriberRead();
    void callbackSubscriberWrite();
    void callbackSubscriberConnect();
    void callbackSubscriberDisconnect();
    void callbackPublisherNotify();

    void parseSubscriberCallback();
    void parsePublisherCallback();
};

#endif /* ESP32_HPP_ */