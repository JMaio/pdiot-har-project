# PDIoT 20/21 Human Activity Recognition project

The `tensorflow` directory contains the Jupyter Notebook file `CNN_training.ipynb` which is used for training the machine learning models used during experimentation. 
The second cell of the notebook contains flags used to select the experiment to be run. Running the notebook will train a model with the selected presets. The submitted version of the file saves the models to `tensorflow/models/cnn_model_{n}_chest_right.tflite` so the experiments can be run without creating many different model files, but this can be changed in the `save_model` function.

Files used for training the model are stored in `tensorflow/data`.
Commented out cells contain code for functions or graphs that were used in the exploration phase of development but are not necessary to run the final models used in the report.


The `backend/README.md` file explains how to set up the server to run live predictions on the app. 
