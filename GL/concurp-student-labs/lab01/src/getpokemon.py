# -*- coding: utf-8 -*-

# Copyright 2021, School of Engineering and Architecture of Fribourg
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

"""
This file contains a function, which downloads a specific Pokemon image from assets.pokemon.com website.
It is a helper funcition that wil be used in the Concurrent Systems lab01.
"""

__author__ = 'Michael Maeder'
__date__ = "04.10.2021"
__version__ = "1.1"
__email__ = "michael.maeder@hefr.ch"
__userid__ = "michael.maeder"


import cv2
import numpy as np
import urllib.request

URL = 'https://assets.pokemon.com/assets/cms2/img/pokedex/detail/'


def download_image(img_nbr: int, path: str, verbose_print: bool = False):
    """
    Downloads a specific pokemon image in PNG format. The function gets a index number and downloads the corresponding
    image. The PNG-converted image will be written to the given filesystem path

    :argument
        img_nbr         -- the number of the image (1 .. 898)
        path            -- the path where the image will be written to
        verbose_print   -- selector if some debugging information should be printed or not

    :exception
        urllib.error.HTTPError  -- will be raised by the request library. The exception will be forwarded to the caller
    """

    try:
        # creation of the request object with the given URL
        req = urllib.request.Request(URL + '{:03d}'.format(img_nbr) + '.png')
        response = urllib.request.urlopen(req)                          # response contains the raw response data
        raw_image_data = response.read()                                # get the bytes of the image
        image = np.asarray(bytearray(raw_image_data), dtype="uint8")    # convert it to an array of uint8s
        image = cv2.imdecode(image, cv2.IMREAD_UNCHANGED)               # the code the array as an image
        cv2.imwrite(path + "/" + '{:03d}'.format(img_nbr) + ".png", image)  # finally write the PNG to the given path
        if verbose_print:
            print("Saved " + '{:03d}'.format(img_nbr) + ".png")
    except Exception as e:
        print("Error occured for Pokemon " + '{:03d}'.format(img_nbr))
        raise e                                                         # forward the exception to the caller
