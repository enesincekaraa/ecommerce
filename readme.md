# Redis Kullanımı için Docker Konfigürasyonu

Projemizin bazı endpointleri daha verimli ve hızlı çalışması için Redis kullanıyoruz. Redis'i kullanmak için aşağıdaki adımları izleyebilirsiniz.

## 1. Docker Compose Konfigürasyonu

`docker-compose.yml` dosyanızı aşağıdaki şekilde oluşturabilirsiniz:

```yaml
version: '3'
services:
  redis:
    image: redis
    container_name: redis
    restart: always
    ports:
      - "6379:6379"
````

## 2. application.yml Konfigürasyonu

`application.yml` dosyanıza aşağıdaki kodları ekleyin:
```yaml
spring:
  data:
    redis:
      port: 6379
      host: redis
````
## 3. Redis CLI ile bağlanma
```yaml
docker exec -it redis redis-cli
````

## 4. Redis Komutları
```yaml
Tüm anahtarları listelemek için
KEYS *
```
```yaml
Belirli bir cache'i görmek için
GET "value::key"
GET "categories::getAllCategories"

```






