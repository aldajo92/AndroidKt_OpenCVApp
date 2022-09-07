#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/imgproc.hpp>
#include <opencv2/imgcodecs.hpp>
#include <opencv2/highgui/highgui.hpp>

#include <jni.h>

using namespace cv;

inline double angle(const cv::Point &pt1, const cv::Point &pt2, const cv::Point &pt0) {
    double dx1 = pt1.x - pt0.x;
    double dy1 = pt1.y - pt0.y;
    double dx2 = pt2.x - pt0.x;
    double dy2 = pt2.y - pt0.y;
    return (dx1 * dx2 + dy1 * dy2) /
           sqrt((dx1 * dx1 + dy1 * dy1) * (dx2 * dx2 + dy2 * dy2) + 1e-10);
}

inline void findDocumentCorners(const cv::Mat &image, std::vector<cv::Point> &corners) {
    std::vector<std::vector<cv::Point>> squares;
    cv::Mat pyr, img, gray0(image.size(), CV_8U), gray;
    cv::pyrDown(image, pyr, cv::Size(image.cols / 2, image.rows / 2));
    cv::pyrUp(pyr, img, image.size());
    std::vector<std::vector<cv::Point>> contours;
    int maxArea = 0;
    for (int c = 0; c < 3; c++) {
        int ch[] = {c, 0};
        cv::mixChannels(&img, 1, &gray0, 1, ch, 1);
        for (int l = 0; l < 11; l++) {
            if (l == 0) {
                cv::Canny(gray0, gray, 0, 50, 5);
                cv::dilate(gray, gray, cv::Mat(), cv::Point(-1, -1));
            } else {
                gray = gray0 >= (l + 1) * 255 / 11.0;
            }
            cv::findContours(gray, contours, cv::RETR_LIST, cv::CHAIN_APPROX_SIMPLE);
            std::vector<cv::Point> approx;
            for (auto &contour: contours) {
                cv::approxPolyDP(contour, approx, cv::arcLength(contour, true) * 0.02, true);
                if (approx.size() == 4 && fabs(contourArea(approx)) > 1000 &&
                    isContourConvex(approx)) {
                    double maxCosine = 0;
                    for (int j = 2; j < 5; j++) {
                        double cosine = fabs(angle(approx[j % 4], approx[j - 2], approx[j - 1]));
                        maxCosine = MAX(maxCosine, cosine);
                    }
                    cv::RotatedRect rect = cv::minAreaRect(approx);
                    int rectArea = rect.boundingRect().area();
                    int imageArea = image.cols * image.rows;
                    if (maxCosine < 0.3 && rectArea < imageArea * 0.7) {
                        squares.push_back(approx);
                        if (rectArea > maxArea) {
                            maxArea = rect.boundingRect().area();
                            corners = approx;
                        }
                    }
                }
            }
        }
    }
}

inline void cropImage(const cv::Mat &image, std::vector<cv::Point> &corners) {

}

extern "C" {
void JNICALL
Java_com_aldajo92_scancv_OpenCVAnalyzer_detectShape(JNIEnv *env,
                                                    jobject instance,
                                                    jlong matAddr) {
    cv::Mat &inputMat = *(cv::Mat *) matAddr;
    std::vector<Point> corners;

    findDocumentCorners(inputMat, corners);

    for (int i = 0; i < corners.size(); ++i) {
        cv::circle(inputMat, corners[i], 6, cv::Scalar(0, 255, 0), 2);
        if (i == 0) {
            cv::line(inputMat, corners[corners.size() - 1], corners[i], cv::Scalar(0, 255, 0), 3);
        } else {
            cv::line(inputMat, corners[i - 1], corners[i], cv::Scalar(0, 255, 0), 3);
        }
    }

//    jobjectArray retobjarr = (jobjectArray)env->NewObjectArray(2, env->FindClass("java/lang/Object"), NULL);
//    return retobjarr;

}

void JNICALL
Java_com_aldajo92_scancv_OpenCVAnalyzer_detectShapeAndCropImage(JNIEnv *env,
                                                                jobject instance,
                                                                jlong matAddr,
                                                                jlong resultAddr) {
    cv::Mat &inputMat = *(cv::Mat *) matAddr;
    cv::Mat &resultInputMat = *(cv::Mat *) resultAddr;
    std::vector<Point> corners;

    cv::Mat lambda( 2, 4, CV_32FC1 );

    findDocumentCorners(inputMat, corners);

    cv::RotatedRect box = cv::minAreaRect(cv::Mat(corners)); // Get the min rectangle area
    Point2f pts[4];
    box.points(pts);

    cv::Point2f src_vertices[3];
    src_vertices[0] = pts[0];
    src_vertices[1] = pts[1];
    src_vertices[2] = pts[3];
    //src_vertices[3] = not_a_rect_shape[3];

    Point2f dst_vertices[3];
    dst_vertices[0] = Point(0, 0);
    dst_vertices[1] = Point(box.boundingRect().width-1, 0);
    dst_vertices[2] = Point(0, box.boundingRect().height-1);

    cv::Mat warpAffineMatrix = getAffineTransform(src_vertices, dst_vertices);

    cv::Size size(box.boundingRect().width, box.boundingRect().height);
    warpAffine(inputMat, resultInputMat, warpAffineMatrix, size, INTER_LINEAR, BORDER_CONSTANT);



    // using perspective transform: http://opencvexamples.blogspot.com/2014/01/perspective-transform.html
//    lambda = getPerspectiveTransform( inputQuad, outputQuad );
//    // Apply the Perspective Transform just found to the src image
//    warpPerspective(inputMat,resultInputMat,lambda,resultInputMat.size() );

    // Setup a rectangle to define your region of interest
    //    cv::Rect myROI(10, 10, 100, 100);
    //    cv::Mat croppedRef(inputMat, myROI);
    //
    //    croppedRef.copyTo(resultInputMat);
}
}
