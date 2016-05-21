

### Source Json Schema

```
{
  "sourceName": "tablename",
  "sourceId": 7,
  "source": {
    "type": "csv",
    "connectionString": "jdbc:mysql://localhost:3306/hr?user=root&password=sam"
  },
  "fields": [
    {
      "fieldName": "sales",
      "fieldId": 1,
      "dataType": "double",
      "valueType": "continuous",
      "min": 0.0,
      "max": 100
    },
    {
      "fieldName": "time",
      "fieldId": 1,
      "dataType": "string",
      "valueType": "time",
      "format": "MM,dd,yy"
    },
    {
      "fieldName": "subject",
      "fieldId": 1,
      "dataType": "string",
      "valueType": "time",
      "possibleValues": [
        "maths",
        "science"
      ]
    }
  ]
}
```
