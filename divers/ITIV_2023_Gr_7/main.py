#!/usr/bin/env python3
# -*- coding: utf-8 -*-

# Copyright 2023, School of Engineering and Architecture of Fribourg
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
Mosaicing-plan
"""

__author__ = "Simon Barras, David Rojas, Damien Steiger"
__date__ = "2023-01-30"
__version__ = "1"
__email__ = "simon.barras@edu.hefr.ch, david.rojas@edu.hefr.ch, damien.steiger@edu.hefr.ch"
__userid__ = "simon.barras, david.rojas, damien.steiger"

# IMPORTS
import argparse
import cv2  # import opencv
import glob
import numpy as np
import colorsys
import demo

# CONSTANTS
THICKNESS = 10  # Thickness of the cross
SIZE = 20  # Size of the cross
COLOR_OFFSET = 0.15  # Percent of the color change of each cross
MARGIN_RATIO = 0.2  # Margin ratio of the image

# GLOBAL VARIABLES
couplePoints: dict = {}  # Dictionary of the points selected by the user


# METHODS
def values_of_cross(seed):
    """
    Return the position of the cross
    :param seed: point selected by the user
    :return: x y, coordinates of the 2 lines
    """
    return {
        'x1': seed[0] - SIZE,
        'y1': seed[1] - SIZE,
        'x2': seed[0] + SIZE,
        'y2': seed[1] + SIZE
    }


def draw_cross(seed, img, index):
    """
    Draw the cross on the image
    :param seed: point selected by the user
    :param img: image to draw
    :param index: index of the point selected
    """
    image = img
    cross = values_of_cross(seed)
    # color from HSL to RGB
    color = colorsys.hls_to_rgb(index * COLOR_OFFSET, 0.5, 1)
    # Multiply by 255 to get the RGB value
    color = tuple([int(x * 255) for x in color])

    # Drawing the lines
    # draw a line ((x;y) point de départ, (x;y) point d'arrivée, couleur, épaisseur)
    cv2.line(image, (cross["x1"], cross["y1"]), (cross["x2"], cross["y2"]), color, THICKNESS)
    cv2.line(image, (cross["x1"], cross["y2"]), (cross["x2"], cross["y1"]), color, THICKNESS)


def my_mouse_callback(evt, x, y, flags, param):
    """
    OpenCV mouse callback to draw the cross and add the point selected by the user to the list of points
    :param evt: openCV event
    :param x: x coordinate of the event
    :param y: y coordinate of the event
    :param flags: openCV flags
    :param param: parameters
    """
    global couplePoints
    if evt == cv2.EVENT_LBUTTONDOWN:
        name = param['window_name']
        img = param['image']
        seed = (x, y)
        draw_cross(seed, img, len(couplePoints[name]))
        couplePoints[name].append(seed)
        cv2.imshow(name, img)  # Show image (window_name, image)


def find_my_own_homography(cp_add, cp_base):
    """
    Compute the homography with a couple of points
    :param cp_add: points of the image to add
    :param cp_base: points of the image base
    :return: the homography matrix
    """
    # transform 2D array to 1D array
    target = cp_base.reshape(-1, 1).ravel()
    # format cp_add to the Matrix A
    A = np.zeros((len(cp_add) * 2, 8))
    for i in range(len(cp_add)):
        A[2 * i, 0:2] = cp_add[i]
        A[2 * i, 2] = 1
        A[2 * i, 6:8] = -cp_add[i] * target[2 * i]
        A[2 * i + 1, 3:5] = cp_add[i]
        A[2 * i + 1, 5] = 1
        A[2 * i + 1, 6:8] = -cp_add[i] * target[2 * i + 1]
    # compute the homography m = (AT*A)^-1 * A * target
    m = np.dot(np.dot(np.linalg.inv(np.dot(A.T, A)), A.T), target)
    # reshape m to the homography matrix
    H = np.array([[m[0], m[1], m[2]], [m[3], m[4], m[5]], [m[6], m[7], 1]])
    return H


# Use opencv to merge the images
def image_fusion(cp_base, cp_add, image2add, image_base, by_hand=False):
    """
    Merge the 2 images to create a mosaic with the points selected by the user
    :param cp_base: points of the image base
    :param cp_add:  points of the image to add
    :param image2add: image to add
    :param image_base: basis image
    :param by_hand: indicate if the user has selected the points by hand or not
    :return: the mosaic
    """

    def build_mosaic(ib, i2a, homography):
        """
        Build the mosaic with the 2 images and the homography
        :param ib: basis image
        :param i2a: image to add
        :param homography: homography matrix to modify the image to add
        :return: the mosaic
        """
        margin = int(ib.shape[0] * MARGIN_RATIO)

        # Use warpPerspective to merge the 2 images
        # (Image à transformer, matrice de transformation, taille de l'image de sortie)
        im_out = cv2.warpPerspective(i2a, homography, (ib.shape[1] + 2 * margin, ib.shape[0] + 2 * margin))

        # Place the first image to the center of the mosaic
        im_out[margin:ib.shape[0] + margin, margin:ib.shape[1] + margin] = ib

        return im_out

    def clean_image(img):
        """
        Remove the black borders of the image
        :param img: image to clean
        :return: new image without black borders
        """
        # Convertir l'image en niveaux de gris
        gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
        # Trouver les pixels non noirs
        coords = cv2.findNonZero(gray)
        # Obtenir les coordonnées du rectangle englobant
        x, y, width, height = cv2.boundingRect(coords)
        # Extraire la zone sans bordures noires
        cropped = img[y:y + height, x:x + width]
        return cropped

    # transform cp_base and cp_add to numpy array
    cp_base = np.array(cp_base)
    cp_add = np.array(cp_add)

    # Merge the 2 images with a homography which is found with the 2 sets of points
    if by_hand:
        print("Use our own homography")
        h = find_my_own_homography(cp_add, cp_base)
    else:
        print("Use opencv homography")
        # Trouve la matrice de transformation (Liste des points à transformer, liste des points de destination)
        h, status = cv2.findHomography(cp_add, cp_base)

    final = build_mosaic(image_base, image2add, h)
    return clean_image(final)


def merge_utility():
    """
    Method to interact with the user during the point selection
    :return: True if the user has validated the points, False otherwise
    """

    def help_selection_menu():
        """
        Print the help message when the user need to select 4 couples of points
        """
        print("Hot keys: \n"
              "\tESC - quit the program\n"
              "\tv - validate the points\n"
              "\tr - reset images\n")

    help_selection_menu()

    while True:
        # Wait for a key to be pressed (time limit)
        c = cv2.waitKey(1)  # ESC

        if c == 27:  # ESC
            print("Exiting ...")
            return False, True

        elif c == 118:  # v
            print("Merge with these given points..")
            return True, False

        elif c == 114:  # r
            print("Reset the initial image")
            return False, False


def merge_image(image_base, index_image2add, by_hand):
    """
    Process to select the points and merge the images
    :param image_base: basis image that will be extended
    :param index_image2add: image to add to the basis image
    :param by_hand: indicate if we use opencv or our own homography
    :return: the new basis image
    """
    global couplePoints

    def add_margin(img):
        """
        increase the size of the image by adding black pixels around the image
        :param img: to increase
        :return: new image
        """
        margin = int(img.shape[0] * MARGIN_RATIO)
        # Add black pixels around the image
        # (image, top margin, bottom margin, left margin, right margin, type of border, color of the border)
        return cv2.copyMakeBorder(img, margin, margin, margin, margin, cv2.BORDER_CONSTANT, value=[0, 0, 0, 0])

    def show_image(img, img_init, name):
        """
        Display an image in a window and set the mouse callback
        :param img: image to display
        :param img_init: image initial
        :param name: name of the window
        """

        param = {'window_name': name, 'image': img, 'image_init': img_init}

        # Create a window with a name (window name, window type)
        cv2.namedWindow(name, cv2.WINDOW_NORMAL)
        # Set the mouse callback (window name, callback function, callback parameter)
        cv2.setMouseCallback(name, my_mouse_callback, param)
        # Display the image (window name, image)
        cv2.imshow(name, img)

    # Read the images (image path, image type)
    image2add = cv2.imread(index_image2add, cv2.IMREAD_COLOR)

    # Initialize values
    image_base_name = "base image"
    image2add_name = "image to add"
    aborting = False

    while not aborting:
        couplePoints = {image_base_name: [], image2add_name: []}

        # Display the images
        # Close all the windows
        cv2.destroyAllWindows()
        canvas = add_margin(image_base)
        show_image(canvas.copy(), canvas, image_base_name)
        show_image(image2add.copy(), image2add, image2add_name)

        # Wait for the user to validate the points
        validate, aborting = merge_utility()
        if not validate:
            continue

        print("Points of image base :", str(couplePoints[image_base_name]))
        print("----------------------")
        print("Points of image to add :", str(couplePoints[image2add_name]))

        # Check if a couple of points length is the same and greater than 4
        if len(couplePoints[image_base_name]) == len(couplePoints[image2add_name]) and len(
                couplePoints[image_base_name]) >= 4:
            image_base = image_fusion(couplePoints[image_base_name], couplePoints[image2add_name], image2add,
                                      image_base, by_hand)
            break
        else:
            print(f"You must select the same number of points on both images and at least 4 (base image:"
                  f" {len(couplePoints[image_base_name])}, image to add: {len(couplePoints[image2add_name])})\n")
    return image_base


def main(image_list, custom_homography, output_file):
    """
    Main function
    :param image_list: images path list to merge
    :param custom_homography: use our own homography or opencv homography
    :param output_file: filename of the final image
    :return:
    """
    image_base = cv2.imread(image_list[0], cv2.IMREAD_COLOR)  # Read the file

    # for image in images:
    for i in image_list[1:]:  # If we want all images except the first one --> image_list[1:]
        image_base = merge_image(image_base, i, custom_homography)

    # Save the final image
    if output_file:
        # Write the image (image path, image)
        cv2.imwrite(output_file, image_base)

    # Show the final image
    cv2.destroyAllWindows()
    cv2.namedWindow("Final", cv2.WINDOW_NORMAL)
    cv2.imshow("Final", image_base)
    cv2.waitKey(0)
    cv2.destroyAllWindows()


if __name__ == '__main__':
    # Define arguments
    parser = argparse.ArgumentParser(description='Tool to merge images together')
    parser.add_argument('-a', '--automatic', default=False, action='store_true', help='Demonstrate the Mosaicing-plan '
                                                                                      'with pre-selected points')
    parser.add_argument('-c', '--custom', default=False, action='store_true', help='Use our homography computation '
                                                                                   'instead of opencv')
    parser.add_argument('-d', '--directory', default="./", type=str, help='Directory containing the images to merge')
    parser.add_argument('-o', '--output', default=None, type=str, help='Output file name')
    # Images as parameters
    parser.add_argument('images', type=str, nargs='*', help='Images to merge')

    # Parse arguments
    args = parser.parse_args()

    # Build the list of images
    imgs = []
    if args.images:
        for im in args.images:
            imgs.append(args.directory + im)
    else:
        imgs = sorted(glob.glob(args.directory + "/*.jpg"))

    # Call the main function
    if args.automatic:
        # Demonstrate the Mosaicing-plan with pre-selected points
        demo.main(imgs, args.custom, args.output)
    else:
        main(imgs, args.custom, args.output)
