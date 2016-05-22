
#### New DataSource api : /api/dataSources

##### Request

```
{
  "action": "new",
  "data": {
    "sourceName": "test",
    "sourceType": "csv",
    "sourcePath": ""
  }
}
```

##### Response 

```
{
  "status": "ok",
  "data": {
    "sourceId": 3,
    "sourceName": "test2",
    "sourceType": "csv",
    "sourcePath": ""
  }
}
```

#### Get DataSource api : /api/dataSources

##### Request

```
{
  "action": "get",
  "sourceId": 2
}
```

##### Response 

```
{
  "status": "ok",
  "data": {
    "sourceType": "csv",
    "sourceName": "test",
    "sourcePath": "",
    "fields": [{
      "fieldId": 1,
      "valueType": "dimensions",
      "fieldName": "page",
      "dataType": "string",
      "sourceId": 2
    }, {
      "fieldId": 2,
      "valueType": "dimensions",
      "fieldName": "url",
      "dataType": "string",
      "sourceId": 2
    }, {
      "fieldId": 3,
      "valueType": "measure",
      "fieldName": "latency",
      "dataType": "double",
      "sourceId": 2
    }, {
      "fieldId": 4,
      "valueType": "dimensions",
      "fieldName": "city",
      "dataType": "String",
      "sourceId": 2
    }, {
      "fieldId": 5,
      "valueType": "time",
      "fieldName": "time",
      "dataType": "String",
      "sourceId": 2
    }, {
      "fieldId": 6,
      "valueType": "measure",
      "fieldName": "test",
      "dataType": "double",
      "sourceId": 2
    }],
    "sourceId": 2
  }
}
```

#### New Field api : /api/fields

##### Request

```
{
  "action": "new",
  "data": {
    "valueType": "dimensions", //measure,dimensions,time
    "fieldName": "city",
    "dataType": "String", // string,double,long
    "sourceId": 2
  }
}
```

##### Response

```
{
  "status": "ok",
  "data": {
    "fieldId": 6,
    "valueType": "measure",
    "fieldName": "test",
    "dataType": "double",
    "sourceId": 2
  }
}
```

#### New Batch Field api : /api/fields

##### Request

```
{
  "action": "newBatch",
  "data": [
    {
      "valueType": "dimensions",
      "fieldName": "page",
      "dataType": "string",
      "sourceId": 3
    },
    {
      "valueType": "dimensions",
      "fieldName": "url",
      "dataType": "string",
      "sourceId": 3
    },
    {
      "valueType": "measure",
      "fieldName": "latency",
      "dataType": "double",
      "sourceId": 3
    },
    {
      "valueType": "dimensions",
      "fieldName": "city",
      "dataType": "String",
      "sourceId": 3
    },
    {
      "valueType": "time",
      "fieldName": "time",
      "dataType": "String",
      "sourceId": 3
    },
    {
      "valueType": "measure",
      "fieldName": "test",
      "dataType": "double",
      "sourceId": 3
    }
  ]
}
```

##### Response

```
{
  "status": "ok",
  "data": [{
    "fieldId": 7,
    "valueType": "dimensions",
    "fieldName": "page",
    "dataType": "string",
    "sourceId": 3
  }, {
    "fieldId": 8,
    "valueType": "dimensions",
    "fieldName": "url",
    "dataType": "string",
    "sourceId": 3
  }, {
    "fieldId": 9,
    "valueType": "measure",
    "fieldName": "latency",
    "dataType": "double",
    "sourceId": 3
  }, {
    "fieldId": 10,
    "valueType": "dimensions",
    "fieldName": "city",
    "dataType": "String",
    "sourceId": 3
  }, {
    "fieldId": 11,
    "valueType": "time",
    "fieldName": "time",
    "dataType": "String",
    "sourceId": 3
  }, {
    "fieldId": 12,
    "valueType": "measure",
    "fieldName": "test",
    "dataType": "double",
    "sourceId": 3
  }]
}
```

#### Get Field api : /api/fields

##### Request

```
{
  "action": "get",
  "fieldId":2
}
```

##### Response 

```
{
  "status": "ok",
  "data": {
    "fieldId": 2,
    "valueType": "dimensions",
    "fieldName": "city",
    "dataType": "String",
    "sourceId": 2
  }
}
```

