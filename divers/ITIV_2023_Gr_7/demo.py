import argparse
import cv2
import glob
import numpy as np
import colorsys

THICKNESS = 5
COLOR_OFFSET = 0.15
MARGIN_RATIO = 0.2

isModeCleanImage = False

couplePoints: dict = {}
storedPoints: dict = {
    "../resources/image_left.jpg": {
        'base image':
            [(813, 2732), (1646, 2588), (2053, 2945), (2072, 4123), (819, 3759), (1229, 1935)],
        'image to add':
            [(1395, 1960), (2231, 1810), (2654, 2176), (2673, 3385), (1399, 3003), (1815, 1141)]
    },
    "../resources/image_right.jpg": {
        'base image':
            [(3495, 2512), (4152, 2261), (4419, 2538), (3830, 4130), (4375, 3687), (3709, 3510)],
        'image to add':
            [(480, 1736), (1195, 1456), (1491, 1751), (861, 3493), (1455, 3013), (724, 2819)]
    }
}

# {'base image': [(812, 2732), (1647, 2588), (2050, 2945), (2070, 4121), (1277, 3907), (1227, 1932)],
#   'image to add': [(1402, 1968), (2240, 1812), (2656, 2186), (2684, 3408), (1874, 3180), (1826, 1156)]}
# {'base image': [(811, 2728), (1643, 2586), (2053, 2946), (2069, 4121), (1278, 3909), (1225, 1929)],
#   'image to add': [(1406, 1974), (2242, 1822), (2664, 2190), (2683, 3401), (1874, 3178), (1822, 1157)]}
# {'base image': [(811, 2736), (1646, 2595), (2058, 2949), (2074, 4133), (1283, 3923), (1231, 1942)],
#   'image to add': [(1397, 1965), (2230, 1810), (2653, 2181), (2681, 3393), (1871, 3168), (1820, 1146)]}


# {'base image': [(3497, 2529), (4154, 2277), (4419, 2545), (3841, 4150), (4378, 3704), (3709, 3527)], 'image to add': [(480, 1736), (1195, 1456), (1491, 1751), (861, 3493), (1457, 3013), (724, 2826)]}



def help():
    print("Hot keys: \n"
          "\tESC - quit the program\n"
          "\tv - validate the points\n"
          "\t0 - clean image\n")


def values_of_cross(seed):
    offset = 10
    return {
        'x1': seed[0] - offset,
        'y1': seed[1] - offset,
        'x2': seed[0] + offset,
        'y2': seed[1] + offset
    }


def draw_cross(seed, img, index):
    image = img
    cross = values_of_cross(seed)
    # color from HSL to RGB
    color = colorsys.hls_to_rgb(index * COLOR_OFFSET, 0.5, 1)
    # Multiply by 255 to get the RGB value
    color = tuple([int(x * 255) for x in color])

    # Drawing the lines
    cv2.line(image, (cross["x1"], cross["y1"]), (cross["x2"], cross["y2"]), color, THICKNESS)
    cv2.line(image, (cross["x1"], cross["y2"]), (cross["x2"], cross["y1"]), color, THICKNESS)


def draw_points(name, img):
    global couplePoints

    # foreach point in the list of points
    for i in range(len(couplePoints[name])):
        seed = couplePoints[name][i]
        draw_cross(seed, img, i)
        cv2.imshow(name, img)


def merge_utility():
    global isModeCleanImage

    is_validate = False

    while not is_validate:
        # cv2.imshow(image_base_name, image_base)  # Show our image inside it.

        c = cv2.waitKey(1)  # ESC

        if c == 27:  # ESC
            print("Exiting ...\n")
            break

        elif c == 118:  # v
            is_validate = True
            print("Merge with these given points...\n")

        elif c == 48:  # 0
            isModeCleanImage = True
            print("Reset the initial image\n")

    return is_validate


# Compute the homography with the 2 lists of points
def find_my_own_homography(cp_add, cp_base):
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
    # compute the homography m = (AT*A)^-1 * A . target
    m = np.dot(np.dot(np.linalg.inv(np.dot(A.T, A)), A.T), target)
    # reshape m to the homography matrix
    H = np.array([[m[0], m[1], m[2]], [m[3], m[4], m[5]], [m[6], m[7], 1]])
    return H


# Use opencv to merge the images
def image_fusion(cp_base, cp_add, image2add, image_base, by_hand=False):
    # transform cp_base and cp_add to numpy array
    cp_base = np.array(cp_base)
    cp_add = np.array(cp_add)

    # Merge the 2 images with a homography which is found with the 2 sets of points
    if by_hand:
        print("Use our own homography")
        h = find_my_own_homography(cp_add, cp_base)
    else:
        print("Use opencv homography")
        h, status = cv2.findHomography(cp_add, cp_base)

    def build_mosaic(ib, i2a, homography):
        margin = int(ib.shape[0] * MARGIN_RATIO)

        # Use warpPerspective to merge the 2 images
        im_out = cv2.warpPerspective(i2a, homography, (ib.shape[1] + 2 * margin, ib.shape[0] + 2 * margin))

        # Place the first image to the center of the mosaic
        im_out[margin:ib.shape[0] + margin, margin:ib.shape[1] + margin] = ib

        return im_out

    def clean_image(img):
        # Convertir l'image en niveaux de gris
        gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)

        # Trouver les pixels non noirs
        coords = cv2.findNonZero(gray)

        # Obtenir les coordonnées du rectangle englobant
        x, y, w, h = cv2.boundingRect(coords)

        # Extraire la zone sans bordures noires
        cropped = img[y:y + h, x:x + w]

        return cropped

    final = build_mosaic(image_base, image2add, h)
    return clean_image(final)


def merge_image(image_base, index_image2add, by_hand):
    global couplePoints

    def add_margin(img):
        # increase the size of the image by adding transparent pixels around the image
        # the size of the margin is 10% of the image size
        margin = int(img.shape[0] * MARGIN_RATIO)
        return cv2.copyMakeBorder(img, margin, margin, margin, margin, cv2.BORDER_CONSTANT, value=[0, 0, 0, 0])

    image2add = cv2.imread(index_image2add, cv2.IMREAD_COLOR)  # Read the file - will keep this initial image

    # Initialize values
    image_base_name = "base image"
    image2add_name = "image to add"
    couplePoints = {image_base_name: storedPoints[index_image2add][image_base_name],
                    image2add_name: storedPoints[index_image2add][image2add_name]}

    def show_image(img, img_init, name):
        cv2.namedWindow(name, cv2.WINDOW_NORMAL)
        draw_points(name, img)
        cv2.imshow(name, img)

    # Display the images
    canva = add_margin(image_base)
    show_image(canva.copy(), canva, image_base_name)
    show_image(image2add.copy(), image2add, image2add_name)

    if not merge_utility():
        return image_base

    # Check if a couple of points length is the same and greater than 4
    if len(couplePoints[image_base_name]) == len(couplePoints[image2add_name]) and len(
            couplePoints[image_base_name]) >= 4:
        return image_fusion(couplePoints[image_base_name], couplePoints[image2add_name], image2add, image_base, by_hand)
    else:
        print(f"You must select the same number of points on both images and at least 4 (base image: {len(couplePoints[image_base_name])}, image to add: {len(couplePoints[image2add_name])})\n")  # raise Exception
        print("Points of image base :", str(couplePoints[image_base_name]))
        print("----------------------")
        print("Points of image to add :", str(couplePoints[image2add_name]))
        merge_utility()
        # raise Exception(
        #     f"You must select the same number of points on both images and at least 4 (base image: {len(couplePoints[image_base_name])}, image to add: {len(couplePoints[image2add_name])})")


def main(image_list, custom_homography, output_file):
    image_init = cv2.imread(image_list[0], cv2.IMREAD_COLOR)  # Read the file - will keap this initial image

    image_base = image_init.copy()

    # for image in images:
    for i in image_list[1:]:  # If we want all images except the first one --> image_list[1:]
        help()  # Shows the list of the keys
        print("Merging...\n")
        image_base = merge_image(image_base, i, custom_homography)
        print("Couple points\n", couplePoints)

    # Save the final image
    if output_file:
        cv2.imwrite(output_file, image_base)

    # Show the final image
    cv2.destroyAllWindows()
    cv2.namedWindow("Final", cv2.WINDOW_NORMAL)
    cv2.imshow("Final", image_base)
    cv2.waitKey(0)
    cv2.destroyAllWindows()


if __name__ == '__main__':
    # Read parameters
    parser = argparse.ArgumentParser(description='Tool to merge images together')
    parser.add_argument('-c', '--custom', default=False, action='store_true', help='Use our homography computation '
                                                                                   'instead of opencv')
    parser.add_argument('-o', '--output', default=None, type=str, help='Output file name')

    args = parser.parse_args()

    parser.print_help()

    # Build the list of images
    res = "../resources/"

    # imgs = sorted(glob.glob(f"{res}*.jpg"))
    imgs = [f"{res}image_center.jpg", f"{res}image_left.jpg", f"{res}image_right.jpg"]

    main(imgs, args.custom, args.output)
