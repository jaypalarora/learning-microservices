# learning-microservices
Spring Boot Microservices Course

#### ElasticSearch
###### Searching index
1. `https://localhost:9200/users-ms-2024.04.23/_search?q=*&format&pretty` where `users-ms-2024.04.23` is the index name.
2. `https://localhost:9200/_cat` prints all the APIs available in ES. For further details on cat refer https://www.elastic.co/guide/en/elasticsearch/reference/current/cat.html
3. `https://localhost:9200/_cat/indices` prints all the indices in ES.
4. `https://localhost:9200/albums-ms-2024.04.23/_search?q=message:eureka` searches the indexed logs for logs with key
   `message` and value `eureka`.
5. 