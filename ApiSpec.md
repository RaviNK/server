
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

