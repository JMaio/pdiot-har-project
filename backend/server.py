# from models import RespeckModel
from flask import Flask, request
# from flask_restful import Resource , Api
# from flask_restful_swagger_3 import Api, Resource, swagger
from flask_restplus import fields, Resource, Api, reqparse
# from werkzeug.exceptions import BadRequest


# https://stackoverflow.com/a/22772916/9184658
from collections import deque


API_PREFIX = '/api/v1'
# https://flask-restplus.readthedocs.io/en/stable/api.html?highlight=swagger.json#flask_restplus.Api.specs_url
OPENAPI_FILE = 'openapi.json'
# # https://pypi.org/project/flask-restful-swagger-3/
# SWAGGER_URL = ''  # URL for exposing Swagger UI (without trailing '/')
# API_URL = ''  # Our API url (can of course be a local resource)
WINDOW_SIZE = 25

app = Flask(__name__)
api = Api(
    app,
    prefix=API_PREFIX,
    # specs_url=OPENAPI_FILE
)

data = {
    "ABC": deque([[0]*WINDOW_SIZE], maxlen=WINDOW_SIZE)
}

parser = reqparse.RequestParser()
parser.add_argument('respeck_data', type=list, location='json', required=True)

respeckData = api.model('RespeckData', {
    'respeck_data': fields.List(fields.List(fields.Float(required=True)))
})


@api.route('/respeck/<string:respeck_mac>')
class RespeckData(Resource):
    @api.doc(responses={
        200: 'Success',
        400: 'Respeck not found'
    })
    def get(self, respeck_mac):
        try:
            r = {respeck_mac: data[respeck_mac]}
            return r, 200
        except KeyError:
            return {}, 404

    @api.expect(respeckData)
    def post(self, respeck_mac):
        args = parser.parse_args()
        d = args['respeck_data']
        print(args)
        # print(d)
        try:
            data[respeck_mac].extend(d)
        except KeyError:
            data[respeck_mac] = deque(d, maxlen=WINDOW_SIZE)

        return {
            'mac': respeck_mac,
            'data': list(data[respeck_mac])
        }


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