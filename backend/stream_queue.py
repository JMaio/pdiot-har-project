# https://stackoverflow.com/a/23510219/9184658

from queue import Queue

class StreamWriter(object):
    def __init__(self):
        self.queue = Queue()

    def write(self, str):
        self.queue.put(str)

    def writelines(self, lines):
        for line in lines:
            self.write(line)

    def read(self):
        str = self.queue.get()
        self.queue.task_done()
        if str == '~':
            return None
        return str

    def close(self):
        self.write('~')  # indicate EOF


# def generate_response(serialize):
#     file = StreamWriter()
#     def serialize_task():
#         serialize(file)
#         file.close()
#     threading.Thread(target=serialize_task).start()
#     while True:
#         chunk = file.read()
#         if chunk is None:
#             break
#         yield chunk
