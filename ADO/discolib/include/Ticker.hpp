// Copyright 2021 Haute école d'ingénierie et d'architecture de Fribourg
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
 * @file Ticker.hpp
 * @author Barras Simon <simon.barras@edu.hefr.ch>, Terreaux Nicolas <nicolas.terreaux@edu.hefr.ch>
 *
 * @brief Interface for the Ticker
 *
 * @date 2021-12-20
 * @version 0.1.0
 ***************************************************************************/

#ifndef TICKER_HPP_
#define TICKER_HPP_

class Ticker {
   public:
    Ticker();
    virtual ~Ticker(){};
    virtual void Tick(){};
};

#endif /* TICKER_HPP_ */
