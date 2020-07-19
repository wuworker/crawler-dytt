# es

## 
PUT /dytt
```json
{
  "settings": {
    "number_of_shards": "5",
    "number_of_replicas": "1",
    "analysis": {
      "analyzer": {
        "pinyin_analyzer": {
          "tokenizer": "my_pinyin",
          "filter": [
            "stop_filter",
            "len_filter"
          ]
        }
      },
      "tokenizer": {
        "my_pinyin": {
          "type": "pinyin",
          "keep_first_letter": true,
          "keep_separate_first_letter": false,
          "limit_first_letter_length": 8,
          "keep_full_pinyin": true,
          "keep_joined_full_pinyin": true,
          "keep_none_chinese": true,
          "keep_none_chinese_together": true,
          "keep_none_chinese_in_first_letter": false,
          "keep_none_chinese_in_joined_full_pinyin": false,
          "none_chinese_pinyin_tokenize": true,
          "keep_original": true,
          "lowercase": true,
          "trim_whitespace": true,
          "remove_duplicated_term": false
        }
      },
      "filter": {
        "stop_filter": {
          "type": "stop",
          "stopwords": "_english_"
        },
        "len_filter": {
          "type": "length",
          "min": 2,
          "max":8
        }
      }
    }
  },
  "mappings": {
    "_doc": {
      "properties": {
        "act": {
          "type": "text",
          "analyzer": "english",
          "fields": {
            "pinyin": {
              "type": "text",
              "analyzer": "pinyin_analyzer"
            }
          }
        },
        "awards": {
          "type": "text",
          "analyzer":"ik_max_word",
          "search_analyzer":"ik_smart"
        },
        "category": {
          "type": "keyword"
        },
        "desc": {
          "type": "text",
          "index": false
        },
        "director": {
          "type": "text",
          "analyzer": "english",
          "fields": {
            "pinyin": {
              "type": "text",
              "analyzer": "pinyin_analyzer"
            }
          }
        },
        "downLinks": {
          "type": "text",
          "index": "false"
        },
        "fileSize": {
          "type": "double"
        },
        "id": {
          "type": "keyword"
        },
        "language": {
          "type": "keyword"
        },
        "name": {
          "type": "text",
          "analyzer":"ik_max_word",
          "search_analyzer":"ik_smart"
        },
        "originPlace": {
          "type": "keyword"
        },
        "picUrl": {
          "type": "text",
          "index": false
        },
        "publishDate": {
          "type": "date"
        },
        "releaseDates": {
          "properties": {
            "date": {
              "type": "date"
            },
            "place": {
              "type": "keyword"
            }
          }
        },
        "score": {
          "type": "double"
        },
        "scoreNums": {
          "type": "integer"
        },
        "screenwriter": {
          "type": "text",
          "analyzer": "english",
          "fields": {
            "pinyin": {
              "type": "text",
              "analyzer": "pinyin_analyzer"
            }
          }
        },
        "tags": {
          "type": "keyword"
        },
        "title": {
          "type": "text",
          "analyzer":"ik_max_word",
          "search_analyzer":"ik_smart"
        },
        "translateNames": {
          "type": "text",
          "analyzer":"ik_max_word",
          "search_analyzer":"ik_smart"
        },
        "url": {
          "type": "text",
          "index": false
        },
        "words": {
          "type": "keyword"
        },
        "year": {
          "type": "integer"
        },
        "updateTime": {
          "type": "date"
        }
      }
    }
  }
}

```