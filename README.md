# Data dependency POC

# My Spring Application

This is a README file for the data dependency POC, build as part of a master thesis project.

## API Endpoints

### Analyze data dependency

- URL: `/analyze`
- Method: POST

**Description:**
This endpoint is used to analyze data dependency for all metrics.

**Request:**
- Content-Type: `application/json`

| Parameter   | Type | Description                |
|-------------|------|----------------------------|
| data        | List | List of zipkin traces      |


### Analyze data dependency using a file

- URL: `/analyze/file`
- Method: GET

**Description:**
This endpoint is used to analyze data dependency for all metrics, using a data.json file in the resources map.


