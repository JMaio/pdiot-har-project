# from models import RespeckModel
from flask import Flask, request, Response
# from flask_restful import Resource , Api
# from flask_restful_swagger_3 import Api, Resource, swagger
from flask_restx import fields, Resource, Api, reqparse
# from werkzeug.exceptions import BadRequest
from flask_cors import CORS
import time
import json
from stream_queue import StreamWriter
from predictor import Predictor
from pathlib import Path
import numpy as np

# https://stackoverflow.com/a/22772916/9184658
from collections import deque


API_PREFIX = '/api/v1'
# https://flask-restplus.readthedocs.io/en/stable/api.html?highlight=swagger.json#flask_restplus.Api.specs_url
OPENAPI_FILE = 'openapi.json'
# # https://pypi.org/project/flask-restful-swagger-3/
# SWAGGER_URL = ''  # URL for exposing Swagger UI (without trailing '/')
# API_URL = ''  # Our API url (can of course be a local resource)
WINDOW_SIZE = 100

p = Path('./tensorflow/models') 
MODEL_NAME = 'cnn_model_fft_filtered01_sf_nomove_2_Chest_Right.tflite'
model = p / MODEL_NAME

interpreter = Predictor(str(model.absolute()))

app = Flask(__name__)
api = Api(
    app,
    prefix=API_PREFIX,
)
app.config.SWAGGER_SUPPORTED_SUBMIT_METHODS = ["get", "post"]
# https://stackoverflow.com/a/59510677/9184658
CORS(app, resources={r'/*': {'origins': '*'}})

data = {
    "C0-FF-EE": deque([[0, 0, 0]]*WINDOW_SIZE, maxlen=WINDOW_SIZE)
}

streams = {
    "C0-FF-EE": StreamWriter()
}

streams['C0-FF-EE'].write([4, 5, 6])

# parser = reqparse.RequestParser()
# parser.add_argument('respeck_data', type=list, location='json')
# parser.add_argument('Content-Type', location='headers')

respeckData = api.model('RespeckData', {
    # 'mac': fields.String,
    'respeck_data': fields.List(fields.List(fields.Float(required=True)))
})

# respeckData = api.model('RespeckData', {
#     'respeck_data': {
#         'timestamp': fields.Integer,
#         'x': fields.Float,
#         'y': fields.Float,
#         'z': fields.Float,
#     }
# })
respeckPrediction = api.model('RespeckPrediction', {
    'predictions': fields.List(fields.Float),
    'label': fields.Integer,
    'activity': fields.String,
})

# @api.route('/respeck', defaults={'respeck_mac': ""})
@api.route('/respeck/<string:respeck_mac>')
class RespeckData(Resource):

    @api.doc(responses={
        400: 'Respeck not found'
    })
    @api.produces(["application/json"])
    @api.marshal_with(respeckData, code=200, description='Respeck data found')
    def get(self, respeck_mac):
        try:
            r = {
                # 'mac': respeck_mac,
                'respeck_data': list(data[respeck_mac])
            }
            return r, 200
        except KeyError:
            return {}, 404

    # @api.expect(parser)
    # @api.consumes(["application/json"])
    @api.expect(respeckData)
    @api.marshal_with(respeckPrediction, code=200, description='Model prediction')
    @api.produces(["application/json"])
    def post(self, respeck_mac):
        # print(request.data)
        # print(request.headers)
        j = request.json
        # print(j)

        d = j['respeck_data']

        try:
            data[respeck_mac].extend(d)
            streams[respeck_mac].writelines(d)
        except KeyError:
            data[respeck_mac] = deque(d, maxlen=WINDOW_SIZE)
            s = StreamWriter()
            s.writelines(d)
            streams[respeck_mac] = s

        npdata = np.array(d, dtype=np.float32).reshape((-1, 3))
        print(npdata.shape)
        p, l, a = interpreter.make_prediction_on_data(npdata, fft=True)

        res = {
            # 'mac': respeck_mac,
            # 'respeck_data': list(data[respeck_mac])
            'predictions': p.flatten().tolist(),
            'label': l.item(),
            'activity': a,
        }
        print(res)

        return res

respeckStreamedData = api.model('RespeckStreamedData', {
    # 'mac': fields.String,
    'respeck_data': fields.List(fields.List(fields.Float(required=True)))
})

@api.route('/respeck/stream/<string:respeck_mac>')
class RespeckStreamedData(Resource):

    @api.doc(responses={
        400: 'Respeck not found'
    })
    @api.produces(["application/json"])
    @api.response(200, 'Streaming Respeck data', fields.List(fields.Float))
    # @api.marshal_with(respeckData, code=200, description='Respeck data found')
    def get(self, respeck_mac):
        
        # def serialize_task():
        #     serialize(file)
        #     file.close()
        # threading.Thread(target=serialize_task).start()
        # while True:
        #     chunk = file.read()
        #     if chunk is None:
        #         break
        #     yield chunk
        # print("hi")
        def stream():
            while True:
                # try:
                chunk = streams[respeck_mac].read()
                if chunk is None:
                    break
                j = json.dumps(chunk)
                # print(j)
                yield j + '\n'
                # except Exception as e:
                #     print(e)
            # for datapoint in list(data[respeck_mac]):
            #     # print("hi")
            #     yield (json.dumps(datapoint)+'\n') #[[1, 2, 3]]
            #     time.sleep(0.1)
        try:
            
            # print("try")
            # r = {
            #     # 'mac': respeck_mac,
            #     'respeck_data': list(data[respeck_mac])
            return Response(stream(), content_type="application/json")
            # }
        except KeyError:
            return {}, 404

@api.route('/data')
class FullData(Resource):
    def get(self):
        return {k: list(v) for (k, v) in data.items()}


if __name__ == '__main__':
    app.run(
        host="0.0.0.0",
        port=5000,
        debug=True,
    )