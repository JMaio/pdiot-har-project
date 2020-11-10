# For more information, please refer to https://aka.ms/vscode-docker-python
FROM python:3.7-slim-buster

EXPOSE 5000

# Keeps Python from generating .pyc files in the container
ENV PYTHONDONTWRITEBYTECODE=1

# Turns off buffering for easier container logging
ENV PYTHONUNBUFFERED=1

RUN python -m pip install pipenv
ADD Pipfile* ./
# Install requirements with pipenv
RUN pipenv install --system --deploy

WORKDIR /app
RUN PATH=$PATH:/app

ADD . /app


# Switching to a non-root user, please refer to https://aka.ms/vscode-docker-python-user-rights
RUN useradd appuser && chown -R appuser /app
USER appuser

# During debugging, this entry point will be overridden. For more information, please refer to https://aka.ms/vscode-docker-python-debug
# CMD ["gunicorn", "--bind", "0.0.0.0:5000", "backend\server:app"]

# https://blog.entirely.digital/docker-gunicorn-and-flask/
# ENTRYPOINT ["gunicorn", "--config", "gunicorn_config.py", "wsgi:app"]

