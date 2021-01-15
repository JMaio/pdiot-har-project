# Backend

## Tech

This folder contains a Python REST API made using [Flask](https://flask.palletsprojects.com/) and [Flask-RESTX](https://flask-restx.readthedocs.io/).

Flask-RESTX makes it easy for the server to conform to the [OpenAPI Specification 3.0](https://swagger.io/specification/) (OAS 3.0), which creates a "blueprint" of the server's endpoints and request-response expectations.

Using the OpenAPI specification enables automatic generation of *client libraries*, such as for Android and JavaScript, removing the need to manually re-implement the same API in multiple languages or platforms.

## Installing and running with Python

Requires `python3.7` - other versions *should* be compatible.
Dependencies are managed using [`pipenv`](https://pipenv.pypa.io/).
To run the app, do the following:

1. Install `pipenv` and follow the next steps, or manually install the packages in the `Pifpile` and skip to the last step.

2. After `pipenv` has been installed, navigate to **this** folder (`./backend`) and:

    ```sh
    pipenv install
    ```

    This might take a few minutes (from installing the full `tensorflow` package).

3. Once all packages have completed installing, run the server:

    ```sh
    # (with pipenv)
    pipenv run python app.py
    ```
    ```sh
    # (or without pipenv)
    python app.py
    ```

## Configuration

To change the model used for inference, as well as window size, use the `MODEL_NAME` and `WINDOW_SIZE` variables at the top of `app.py`.
Models are located in the `tensorflow/models` directory.

Model inference is executed in the `RespeckData.post` method - when given a window of accelerometer data `(x, y, z)`, this function will return a JSON reponse describing the model prediction for which type of activity is currently being detected.

## Running with Docker

TODO