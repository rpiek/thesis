# Data dependency POC

This is a README file for the data dependency POC, build as part of a master thesis project.

## API Endpoints

### Analyze data dependency

- URL: `/data-dependency/analyze`
- Method: POST

**Description:**
This endpoint is used to analyze data dependency for all metrics.

**Request:**
- Content-Type: `application/json`

| Parameter   | Type | Description                |
|-------------|------|----------------------------|
| data        | List | List of zipkin traces      |

### Analyze read data dependency

- URL: `/data-dependency/read`
- Method: POST

**Description:**
This endpoint is used to analyze read data dependency for DataDepends.

**Request:**
- Content-Type: `application/json`

| Parameter   | Type | Description                |
|-------------|------|----------------------------|
| data        | List | List of zipkin traces      |

### Analyze write data dependency

- URL: `/data-dependency/write`
- Method: POST

**Description:**
This endpoint is used to analyze write data dependency for DataDepends.

**Request:**
- Content-Type: `application/json`

| Parameter   | Type | Description                |
|-------------|------|----------------------------|
| data        | List | List of zipkin traces      |

### Analyze  data dependends need

- URL: `/data-dependency/need`
- Method: POST

**Description:**
This endpoint is used to analyze the DataDependsNeed metric.

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


