# Dynamodb test (Music API)

API para testes de consultas com a SDK da AWS para o DynamoDB.

## Status do Projeto

![Static Badge](https://img.shields.io/badge/status-em%20desenvolvimento-brightgreen?style=for-the-badge&logo=appveyor)

## Funcionalidades

- Cadastro de músicas;
- Consultas de músicas:
  - [load](https://www.javadoc.io/doc/com.amazonaws/aws-java-sdk-dynamodb/1.11.519/com/amazonaws/services/dynamodbv2/datamodeling/DynamoDBMapper.html#load-java.lang.Class-java.lang.Object-java.lang.Object-com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig-)
  - [query](https://www.javadoc.io/doc/com.amazonaws/aws-java-sdk-dynamodb/1.11.519/com/amazonaws/services/dynamodbv2/datamodeling/DynamoDBMapper.html#query-java.lang.Class-com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression-com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig-)

## Documentação da API

```yaml
{
  "openapi": "3.0.1",
  "info": {
    "title": "Music API",
    "description": "This api serves as a test for the aws sdk for dynamodb functions (query, load and save).",
    "contact": {
      "name": "Gustavo Silva",
      "url": "https://github.com/gasfgrv",
      "email": "gustavo_almeida11@hotmail.com"
    },
    "version": "1.0"
  },
  "servers": [
    {
      "url": "http://localhost:8080",
      "description": "Server URL in Development environment"
    }
  ],
  "paths": {
    "/music": {
      "post": {
        "tags": [
          "Music"
        ],
        "operationId": "saveMusic",
        "requestBody": {
          "content": {
            "application/json": {
              "schema": {
                "$ref": "#/components/schemas/MusicRequest"
              }
            }
          },
          "required": true
        },
        "responses": {
          "200": {
            "description": "OK",
            "content": {
              "*/*": {
                "schema": {
                  "$ref": "#/components/schemas/MusicResponse"
                }
              }
            }
          }
        }
      }
    },
    "/music/find": {
      "get": {
        "tags": [
          "Music"
        ],
        "operationId": "loadMusic",
        "parameters": [
          {
            "name": "song_title",
            "in": "query",
            "required": true,
            "schema": {
              "type": "string"
            }
          },
          {
            "name": "artist",
            "in": "query",
            "required": true,
            "schema": {
              "type": "string"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "content": {
              "*/*": {
                "schema": {
                  "$ref": "#/components/schemas/MusicResponse"
                }
              }
            }
          }
        }
      }
    },
    "/music/filter": {
      "get": {
        "tags": [
          "Music"
        ],
        "operationId": "queryMusics",
        "parameters": [
          {
            "name": "song_title",
            "in": "query",
            "required": true,
            "schema": {
              "type": "string"
            }
          },
          {
            "name": "artist",
            "in": "query",
            "required": false,
            "schema": {
              "type": "string"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "content": {
              "*/*": {
                "schema": {
                  "type": "array",
                  "items": {
                    "$ref": "#/components/schemas/MusicsResponse"
                  }
                }
              }
            }
          }
        }
      }
    }
  },
  "components": {
    "schemas": {
      "MusicRequest": {
        "required": [
          "song_title"
        ],
        "type": "object",
        "properties": {
          "song_title": {
            "type": "string"
          },
          "artist": {
            "type": "string"
          },
          "written_by": {
            "type": "array",
            "items": {
              "type": "string"
            }
          },
          "produced_by": {
            "type": "array",
            "items": {
              "type": "string"
            }
          },
          "album": {
            "type": "string"
          },
          "released_in": {
            "type": "integer",
            "format": "int32"
          }
        }
      },
      "MusicResponse": {
        "type": "object",
        "properties": {
          "song_title": {
            "type": "string"
          },
          "artist": {
            "type": "string"
          },
          "written_by": {
            "type": "array",
            "items": {
              "type": "string"
            }
          },
          "produced_by": {
            "type": "array",
            "items": {
              "type": "string"
            }
          },
          "album": {
            "type": "string"
          },
          "released_in": {
            "type": "integer",
            "format": "int32"
          }
        }
      },
      "MusicsResponse": {
        "type": "object",
        "properties": {
          "song_title": {
            "type": "string"
          },
          "artist": {
            "type": "string"
          },
          "album": {
            "type": "string"
          },
          "released_in": {
            "type": "integer",
            "format": "int32"
          }
        }
      }
    }
  }
}
```

## Variáveis de Ambiente

Para rodar esse projeto, você vai precisar adicionar as seguintes variáveis de ambiente.

- `AWS_ACCESS_KEY`: ID de chave de acesso para autenticação na AWS
- `AWS_REGION`: Região onde se encontram recursos na AWS
- `AWS_SECRET_KEY`: Chave de acesso secreta para autenticação na AWS
- `AWS_SERVICE_ENDPOINT`: URL do ponto de entrada para um serviço da Web da AWS

## Rodando localmente

Clone o projeto

```bash
  git clone git@github.com:gasfgrv/dynamodb-test.git
```

Entre no diretório do projeto

```bash
  cd dynamodb-test
```

Instale as dependências

```bash
  mvn clean package -DskipTests
```

Subir recursos na AWS

```bash
  cd infra/
  terraform init
  terraform plan
  terraform apply -auto-approve
```

Inicie a aplicação, pode ser pela IDE (Intellij ou Eclipse), ou rodando o seguinte comando:

```bash
    java \
    -Daws.serviceEndpoint=${AWS_SERVICE_ENDPOINT} \
    -Daws.signingRegion=${AWS_REGION} \
    -Daws.accessKey=${AWS_ACCESS_KEY} \
    -Daws.secretKey=${AWS_SECRET_KEY} \
    -jar target/dynamodb-test-0.0.1-SNAPSHOT.jar
```

Use a collection para ajudar com os endpoints:

[![Run in Insomnia}](https://insomnia.rest/images/run.svg)](https://insomnia.rest/run/?label=Music%20API&uri=https%3A%2F%2Fraw.githubusercontent.com%2Fgasfgrv%2Fdynamodb-test%2Fmaster%2Fcollection.json)

**Obs:** Para remover os recursos da aws, evitando custos desncessários, use o comando `terraform destroy -auto-approve`
dentro da pasta de infra

## Rodando os testes

Para rodar os testes, rode o seguinte comando

```bash
  mvn tests
```

## Stack utilizada

- Java 17
- Spring Boot 3
- Maven

### Dependências:

- spring web
- lombok
- springdoc-openapi-starter-webmvc-ui
- spring validation
- aws sdk

## Autores

- [@gasfgrv](https://www.github.com/gasfgrv)

