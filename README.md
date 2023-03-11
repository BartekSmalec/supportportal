[![CircleCI](https://dl.circleci.com/status-badge/img/gh/BartekSmalec/supportportal/tree/main.svg?style=svg)](https://dl.circleci.com/status-badge/redirect/gh/BartekSmalec/supportportal/tree/main)
[![codecov](https://codecov.io/gh/BartekSmalec/supportportal/branch/main/graph/badge.svg?token=etJT0Qyq7y)](https://codecov.io/gh/BartekSmalec/supportportal)
![Spotless Java](https://img.shields.io/badge/spotless%20java-CHECKED-green)


# Support Portal

Simple app for managing user accounts. Login by JWT token. More screens of application in screenshot folder. Here’s [link](https://github.com/BartekSmalec/supportportal-front) to frontend repo. [Demo](http://18.185.238.10/) on ec2 instance: 

## Continuous Integration/Continuous Deployment (CI/CD) Pipeline

This repository uses CircleCI to automatically build, test, and deploy code changes.

### Pipeline Overview

The pipeline consists of two jobs that are executed sequentially in a workflow:

1.  **build-and-test**: builds and tests the application using Maven and runs Spotless check.
2.  **deploy-to-ec2**: deploys the application to an EC2 instance using SSH and Docker.

### Job Details

#### Build and Test Job

This job is responsible for building and testing the application. It uses Maven to build the application, runs Spotless check, and uploads the build artifacts to Codecov.

#### Deploy to EC2 Job

This job deploys the application to an EC2 instance using SSH and Docker. It requires the artifacts from the Build and Test job and runs the application on the EC2 instance.

### Workflow

The workflow consists of two jobs that are executed sequentially:

1.  **build-and-test**: runs on all branches.
2.  **deploy-to-ec2**: runs only on the main branch after the Build and Test job completes successfully.

### Configuration

The pipeline is configured using the latest version (2.1) of the CircleCI pipeline process engine. The pipeline configuration is stored in the `.circleci/config.yml` file in the repository.


## Screenshots:

![alt text](https://github.com/BartekSmalec/supportportal/blob/main/screenshots/localhost_4200_%20(2).png?raw=true)
![alt text](https://github.com/BartekSmalec/supportportal/blob/main/screenshots/localhost_8080_swagger-ui_.png?raw=true)



