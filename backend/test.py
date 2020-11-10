import random
from requests import post
import time

class MockRespeck:
    def __init__(self, mac="C0:FF:EE" ,freq=12.5):
        self.freq = freq # 12.5 hz refresh default
        self.mac = mac.replace(':', '-') # bluetooth MAC address

    def get_data(self, window=25):
        d = []
        seed = [
            random.uniform(-1.0, 1.0),
            random.uniform(-1.0, 1.0),
            random.uniform(-1.0, 1.0),
        ]
        prev = seed
        for i in range(window):
            new = list(map(lambda p: p + random.uniform(-.1, .1), prev))
            d.append(new)
            prev = new
        
        return d


if __name__ == "__main__":
    r = MockRespeck()
    url = f'http://localhost:5000/api/v1/respeck/{r.mac}'

    while True:
        print(time.time())
        d = r.get_data(window=25)
        p = post(url, json={'respeck_data': d})
        # time.sleep(1)
