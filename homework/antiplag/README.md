# Антиплагиат  
КПО, Домашняя работа №3  
Синхронное межсервисное взаимодействие

## 1. Краткое описание

Система хранит присланные студентами работы и синхронно проверяет их на плагиат.
Архитектура микросервисная:

* `storage-service` — хранение файлов и метаданных о сдачах.
* `analysis-service` — анализ работ, хранение и выдача отчётов.
* `gateway-service` — API Gateway, единственная точка входа для клиентов.

Все сервисы упакованы в Docker-контейнеры и запускаются через `docker compose`.

---

## 2. Архитектура и взаимодействие сервисов

### 2.1. Сервисы

**1. File Storing Service (`storage-service`)**

* Хранит информацию о сдаче: `id`, `studentName`, `assignmentId`, `filename`, путь к файлу, время загрузки.
* Физически сохраняет файл на файловой системе внутри контейнера (`/data/uploads`).
* Подключен к H2 (файл в томе Docker).

Основные эндпоинты:

* `POST /internal/files`
  Вход: `multipart/form-data` (`file`, `studentName`, `assignmentId`).
  Выход: JSON с метаданными сохранённой работы (без содержимого файла).

* `GET /internal/files/{id}`
  Выход: бинарное содержимое файла по `id`.

---

**2. File Analysis Service (`analysis-service`)**

* Получает идентификатор работы и метаданные.
* Забирает содержимое файла из `storage-service`.
* Считает хэш содержимого и ищет плагиат среди уже сохранённых отчётов по тому же заданию.
* Хранит отчёты в своей базе (H2, отдельный файл).
* Формирует URL для облака слов (QuickChart Word Cloud API).

Основные эндпоинты:

* `POST /internal/analyze`
  Вход (JSON):

  ```json
  {
    "workId": 1,
    "assignmentId": 1,
    "studentName": "Student1"
  }
  ```

  Выход: полный отчёт по работе (внутренний формат).

* `GET /internal/assignments/{assignmentId}/reports`
  Выход: массив отчётов по заданию (внутренний формат, включая `fileHash`, `wordCloudUrl`, `errorMessage`).

---

**3. API Gateway (`gateway-service`)**

* Принимает запросы от «клиента» (условно преподаватель/студент).
* Проксирует запросы к `storage-service` и `analysis-service`.
* Возвращает удобный внешний формат JSON.

Основные эндпоинты:

* `POST /api/works`
  Вход: `multipart/form-data` с полями:

    * `file` — файл работы
    * `studentName` — строка
    * `assignmentId` — целое

  Последовательность внутри:

    1. Отправка файла в `storage-service: POST /internal/files`.
    2. Получение `workId` из ответа storage.
    3. Вызов `analysis-service: POST /internal/analyze` с `workId`, `assignmentId`, `studentName`.
    4. Возврат комбинированного ответа:

       ```json
       {
         "storedWork": { ... },
         "report": { ... }
       }
       ```

* `GET /api/assignments/{assignmentId}/reports`
  Gateway вызывает `analysis-service: GET /internal/assignments/{assignmentId}/reports`, маппит внутренний отчёт в внешний DTO и возвращает список отчётов по заданию.

---

## 3. Алгоритм определения плагиата

В текущей реализации плагиат определяется так:

1. `analysis-service` получает `workId`, `assignmentId`, `studentName`.
2. Через `storage-service` (GET `/internal/files/{id}`) забирается содержимое файла.
3. Считается криптографический хэш содержимого (SHA-256).
4. В таблице отчётов `analysis-service` выполняется поиск всех отчётов с:

    * тем же `assignmentId`;
    * тем же `fileHash`.
5. Если найден более ранний отчёт с таким же хэшом и **другим** `studentName`, то новая работа помечается как плагиат:

    * `plagiarized = true`
    * `status = PLAGIARISM_FOUND`
    * `sourceStudentName`, `sourceWorkId` заполняются из самого раннего найденного отчёта.
6. Если совпадений по хэшу нет, работа считается оригинальной:

    * `plagiarized = false`
    * `status = COMPLETED`
7. Для любой успешно проанализированной работы строится URL облака слов:

    * берётся исходный текст,
    * из него формируется строка для QuickChart API,
    * в отчёт сохраняется `wordCloudUrl`, например:
      `https://quickchart.io/wordcloud?text={...}`.

Таким образом, плагиатом считается ситуация, когда существует **более ранняя сдача** с идентичным содержимым в рамках того же задания.

---

## 4. Сборка и запуск

### 4.1. Предварительные требования

* Docker и docker compose
* JDK 17
* Gradle wrapper уже находится в каждом сервисе

Корень проекта: `antiplag`, внутри три каталога:

* `storage-service`
* `analysis-service`
* `gateway-service`
* `docker-compose.yml`

---

### 4.2. Сборка JAR-файлов

Из корня проекта:

```bash
cd storage-service
./gradlew bootJar

cd ../analysis-service
./gradlew bootJar

cd ../gateway-service
./gradlew bootJar

cd ..
```

---

### 4.3. Запуск инфраструктуры

```bash
docker compose up --build
```

При необходимости можно запустить в фоне:

```bash
docker compose up --build -d
```

После запуска:

* API Gateway: `http://localhost:8080`
* File Storing Service: `http://localhost:8081`
* File Analysis Service: `http://localhost:8082`

---

## 5. Примеры запросов

Ниже приведены команды, которые демонстрируют работу системы и одновременно покрывают критерии проверки.

### 5.1. Отправка работ через API Gateway

#### Работа 1 (Student1, задание 1)

```bash
echo "hello world hello kpo antiplagiat" > /tmp/work1.txt

curl -X POST "http://localhost:8080/api/works" \
  -F "file=@/tmp/work1.txt" \
  -F "studentName=Student1" \
  -F "assignmentId=1"
```

Ожидается `plagiarized: false`, `status: COMPLETED`.

#### Работа 2 (Student2, тот же текст, то же задание 1)

```bash
echo "hello world hello kpo antiplagiat" > /tmp/work2.txt

curl -X POST "http://localhost:8080/api/works" \
  -F "file=@/tmp/work2.txt" \
  -F "studentName=Student2" \
  -F "assignmentId=1"
```

Ожидается:

* `plagiarized: true`
* `status: PLAGIARISM_FOUND`
* `sourceStudentName: "Student1"`
* `sourceWorkId: id работы Student1`.

#### Работа 3 (Student3, другой текст, то же задание 1)

```bash
echo "this is completely different text for kpo homework" > /tmp/work3.txt

curl -X POST "http://localhost:8080/api/works" \
  -F "file=@/tmp/work3.txt" \
  -F "studentName=Student3" \
  -F "assignmentId=1"
```

Ожидается `plagiarized: false`, `status: COMPLETED`.

#### Работа 4 (Student4, тот же текст, но другое задание 2)

```bash
echo "hello world hello kpo antiplagiat" > /tmp/work4.txt

curl -X POST "http://localhost:8080/api/works" \
  -F "file=@/tmp/work4.txt" \
  -F "studentName=Student4" \
  -F "assignmentId=2"
```

Ожидается `plagiarized: false`, так как задание другое (`assignmentId = 2`).

---

### 5.2. Получение отчётов через API Gateway

Отчёты по заданию 1:

```bash
curl "http://localhost:8080/api/assignments/1/reports"
# или более удобно:
curl "http://localhost:8080/api/assignments/1/reports" | jq
```

Отчёты по заданию 2:

```bash
curl "http://localhost:8080/api/assignments/2/reports" | jq
```

Ответ содержит список отчётов с полями:

* `id`
* `workId`
* `assignmentId`
* `studentName`
* `plagiarized`
* `status`
* `sourceStudentName`
* `sourceWorkId`
* `createdAt`

Это реализует сценарий преподавателя: получение аналитики по контрольной работе.

---

### 5.3. Примеры прямых запросов к микросервисам

#### Storage-service

Загрузка напрямую в `storage-service`:

```bash
echo "storage direct test" > /tmp/storage_test.txt

curl -X POST "http://localhost:8081/internal/files" \
  -F "file=@/tmp/storage_test.txt" \
  -F "studentName=Direct" \
  -F "assignmentId=99"
```

Чтение содержимого по `id` (подставить реальный id из ответа выше):

```bash
curl "http://localhost:8081/internal/files/5"
```

#### Analysis-service

Прямой запуск анализа по `workId`:

```bash
curl -X POST "http://localhost:8082/internal/analyze" \
  -H "Content-Type: application/json" \
  -d '{
    "workId": 1,
    "assignmentId": 1,
    "studentName": "DirectStudent"
  }'
```

Получение всех отчётов по заданию 1 из `analysis-service`:

```bash
curl "http://localhost:8082/internal/assignments/1/reports" | jq
```
---
## 6. Завершение работы

Остановка системы:

```bash
docker compose down
```

При необходимости очищения данных (H2 в volume) можно дополнительно удалить созданные тома Docker.
