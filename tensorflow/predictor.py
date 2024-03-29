import tensorflow as tf
import numpy as np
import scipy
import pandas as pd

# enmo and smoothing don't have that much of an effect on performance
class Predictor:
    ACTIVITY_CODE_TO_TFCODE_MAPPING = {
      0:   0,   # "Sitting",
      4:   1,   # "Sitting bent forward",
      5:   2,   # "Sitting bent backward",
      1:   3,   # "Walking at normal speed",
      100: 4,   # "Standing",
      2:   5,   # "Lying down on back",
      7:   6,   # "Lying down left",
      6:   7,   # "Lying down right",
      8:   8,   # "Lying down on stomach",
      9:   9,   # "Movement",
      11:  10,  # "Running",
      12:  11,  # "Climbing stairs",
      13:  12,  # "Descending stairs",
      31:  13,  # "Desk work"
    }

    ACTIVITY_TFCODE_TO_CODE_MAPPING = {
      0:  0,   # "Sitting",
      1:  4,   # "Sitting bent forward",
      2:  5,   # "Sitting bent backward",
      3:  1,   # "Walking at normal speed",
      4:  100, # "Standing",
      5:  2,   # "Lying down on back",
      6:  7,   # "Lying down left",
      7:  6,   # "Lying down right",
      8:  8,   # "Lying down on stomach",
      9:  9,   # "Movement",
      10: 11,  # "Running",
      11: 12,  # "Climbing stairs",
      12: 13,  # "Descending stairs",
      13: 31,  # "Desk work"
    }

    LABELS = {
        0:  "Sitting",
        1:  "Sitting bent forward",
        2:  "Sitting bent backward",
        3:  "Walking at normal speed",
        4:  "Standing",
        5:  "Lying down on back",
        6:  "Lying down left",
        7:  "Lying down right",
        8:  "Lying down on stomach",
        9:  "Movement",
        10: "Running",
        11: "Climbing stairs",
        12: "Descending stairs",
        13: "Desk work"
    }

    CLASSES = {
        0: 0,
        1: 0,
        2: 0,
        3: 1,
        4: 2,
        5: 3,
        6: 3,
        7: 3,
        8: 3,
        9: 4,
        10: 5,
        11: 6,
        12: 7,
        13: 0
    }

    # Grouping
    CLASS_LABELS = {
        0: "Sitting",
        1: "Walking",
        2: "Standing",
        3: "Lying",
        4: "Movement",
        5: "Running",
        6: "Climbing Stairs",
        7: "Descending stairs"
    }

    # No movement class
    CLASS_LABELS_NOMO = {
        0: "Sitting",
        1: "Walking",
        2: "Standing",
        3: "Lying",
        5: "Running",
        6: "Climbing Stairs",
        7: "Descending stairs"
    }


    window_size = 100

    sensor_pos = 'Chest'
    sensor_side = 'Right'

    # the Euclidean norm (vector magnitude) of the three raw signals minus 1, referred to as ENMO;
    def get_enmo(self, data):

        norm = np.sum(np.abs(data)**2,axis=-1)**(1./2) - 1.0
        norm[norm < 0] = 0

        enmo_arr = (np.c_[data, norm])

        return enmo_arr


    # https://stackoverflow.com/questions/20618804/how-to-smooth-a-curve-in-the-right-way
    def smooth_data(self, data, box_pts):
        # box_pts is size of kernel
        box = np.ones(box_pts)/box_pts
        data_smooth = []

        data_smooth = scipy.ndimage.convolve1d(data, box, mode='reflect', axis=0)

        return data_smooth


    # input data after doing any amplitude processing
    def fftransform(self, data):

        #filter_freq = 0.1 * 12.5 / 2

        overlap = 10
        window_size = 24

        window_spec = []

        start_i = 0
        end_i = start_i + window_size

        prev_dc = 0

        while end_i <= data.shape[0]:
            window = data[start_i:end_i] * np.hamming(window_size)[:,None]

            # filtering out low frequencies to remove noise due to gravity
            dc = (0.0158 * window + 0.9843 * prev_dc)
            mag_window = window - dc

            prev_dc = dc

            mag = np.abs(np.fft.fftshift(np.fft.fft(mag_window, axis = 0)))
            
            #mag[mag > filter_freq] = 0

            window_spec.append(mag)

            start_i = end_i - overlap
            end_i = start_i + window_size

        return np.array(window_spec)

    # returns probability for each class for samples in testX
    def get_predictions_from_saved_model(self, model_filename, data):
        # Load the TFLite model and allocate tensors.
        interpreter = tf.lite.Interpreter(model_filename)
        interpreter.allocate_tensors()

        # Get input and output tensors.
        input_details = interpreter.get_input_details()
        output_details = interpreter.get_output_details()

        data = np.array(data,dtype=np.float32)
        interpreter.set_tensor(input_details[0]['index'], [data])

        interpreter.invoke()

        predictions = interpreter.get_tensor(output_details[0]['index'])

        return np.array(predictions)


    # get class labels for prediction array
    def get_prediction_labels(self, predictions):
        pred_labels = np.argmax(predictions)
        return pred_labels


    def make_prediction_on_data(self, data, model_filename, grouped=False, fft=False, enmo=False, smoothing=False, box_pts=3):

        if smoothing:
            data = self.smooth_data(data, box_pts)
        if enmo:
            data = self.get_enmo(data)
        if fft:
            data = self.fftransform(data)

        predictions = self.get_predictions_from_saved_model(model_filename, data)
        label = self.get_prediction_labels(predictions)

        if grouped:
            activity = self.CLASS_LABELS[label]
        else:
            activity = self.LABELS[label]

        return predictions, label, activity


if __name__ == "__main__":
    # test data is data for one window, shape (100,3)
    test_data_file = "test_data.csv"
    with open(test_data_file) as f:
        data = np.array(pd.read_csv(f))

    print(data.shape)
    model_filename = './models/cnn_model_fft_4_Chest_Right.tflite'

    grouped = True
    fft = True # fast fourier transform 
    enmo = False # euclidean norm minus one
    smoothing = False # smoothing with convolution

    predictor = Predictor()
    predictions, label, activity = predictor.make_prediction_on_data(data, model_filename, grouped, fft, enmo, smoothing)

    print("Predictions: ", predictions)
    print("Class label: ", label)
    print("Activity name: ", activity)
