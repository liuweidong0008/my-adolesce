package com.adolesce.cloud.es.constants;

public class IndexMappingConstants {
    public static final String ITCAST_MAPPING = "{\n" +
            "      \"properties\" : {\n" +
            "        \"address\" : {\n" +
            "          \"type\" : \"text\",\n" +
            "          \"analyzer\" : \"ik_max_word\"\n" +
            "        },\n" +
            "        \"age\" : {\n" +
            "          \"type\" : \"long\"\n" +
            "        },\n" +
            "        \"name\" : {\n" +
            "          \"type\" : \"keyword\"\n" +
            "        }\n" +
            "      }\n" +
            "    }";

    public static final String GOODS_MAPPING = "{\n" +
            "\t\"properties\": {\n" +
            "\t\t\"title\": {\n" +
            "\t\t\t\"type\": \"text\",\n" +
            "\t\t\t\"analyzer\": \"ik_smart\"\n" +
            "\t\t},\n" +
            "\t\t\"price\": { \n" +
            "\t\t\t\"type\": \"double\"\n" +
            "\t\t},\n" +
            "\t\t\"createTime\": {\n" +
            "\t\t\t\"type\": \"date\"\n" +
            "\t\t},\n" +
            "\t\t\"categoryName\": {\t\n" +
            "\t\t\t\"type\": \"keyword\"\n" +
            "\t\t},\n" +
            "\t\t\"brandName\": {\t\n" +
            "\t\t\t\"type\": \"keyword\"\n" +
            "\t\t},\n" +
            "\n" +
            "\t\t\"spec\": {\t\t\n" +
            "\t\t\t\"type\": \"object\"\n" +
            "\t\t},\n" +
            "\t\t\"saleNum\": {\t\n" +
            "\t\t\t\"type\": \"integer\"\n" +
            "\t\t},\n" +
            "\t\t\n" +
            "\t\t\"stock\": {\t\n" +
            "\t\t\t\"type\": \"integer\"\n" +
            "\t\t}\n" +
            "\t}\n" +
            "}";


    /**
     * PUT /hotel
     * {
     *   "settings": {
     *     "analysis": {
     *       "analyzer": {
     *         "text_anlyzer": {
     *           "tokenizer": "ik_max_word",
     *           "filter": "py"
     *         },
     *         "completion_analyzer": {
     *           "tokenizer": "keyword",
     *           "filter": "py"
     *         }
     *       },
     *       "filter": {
     *         "py": {
     *           "type": "pinyin",
     *           "keep_full_pinyin": false,
     *           "keep_joined_full_pinyin": true,
     *           "keep_original": true,
     *           "limit_first_letter_length": 16,
     *           "remove_duplicated_term": true,
     *           "none_chinese_pinyin_tokenize": false
     *         }
     *       }
     *     }
     *   },
     *   "mappings": {
     *     "properties": {
     *       "id":{
     *         "type": "keyword"
     *       },
     *       "name":{
     *         "type": "text",
     *         "analyzer": "text_anlyzer",
     *         "search_analyzer": "ik_smart",
     *         "copy_to": "all"
     *       },
     *       "address":{
     *         "type": "keyword",
     *         "index": false
     *       },
     *       "price":{
     *         "type": "integer"
     *       },
     *       "score":{
     *         "type": "integer"
     *       },
     *       "brand":{
     *         "type": "keyword",
     *         "copy_to": "all"
     *       },
     *       "city":{
     *         "type": "keyword"
     *       },
     *       "starName":{
     *         "type": "keyword"
     *       },
     *       "business":{
     *         "type": "keyword",
     *         "copy_to": "all"
     *       },
     *       "location":{
     *         "type": "geo_point"
     *       },
     *       "pic":{
     *         "type": "keyword",
     *         "index": false
     *       },
     *       "isAD" : {
     *         "type" : "boolean"
     *       },
     *       "all":{
     *         "type": "text",
     *         "analyzer": "text_anlyzer",
     *         "search_analyzer": "ik_smart"
     *       },
     *       "suggestion":{
     *           "type": "completion",
     *           "analyzer": "completion_analyzer"
     *       }
     *     }
     *   }
     * }
     */
    public static final String HOTEL_MAPPING = "{\n" +
            "  \"settings\": {\n" +
            "    \"analysis\": {\n" +
            "      \"analyzer\": {\n" +
            "        \"text_anlyzer\": {\n" +
            "          \"tokenizer\": \"ik_max_word\",\n" +
            "          \"filter\": \"py\"\n" +
            "        },\n" +
            "        \"completion_analyzer\": {\n" +
            "          \"tokenizer\": \"keyword\",\n" +
            "          \"filter\": \"py\"\n" +
            "        }\n" +
            "      },\n" +
            "      \"filter\": {\n" +
            "        \"py\": {\n" +
            "          \"type\": \"pinyin\",\n" +
            "          \"keep_full_pinyin\": false,\n" +
            "          \"keep_joined_full_pinyin\": true,\n" +
            "          \"keep_original\": true,\n" +
            "          \"limit_first_letter_length\": 16,\n" +
            "          \"remove_duplicated_term\": true,\n" +
            "          \"none_chinese_pinyin_tokenize\": false\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  },\n" +
            "  \"mappings\": {\n" +
            "    \"properties\": {\n" +
            "      \"id\":{\n" +
            "        \"type\": \"keyword\"\n" +
            "      },\n" +
            "      \"name\":{\n" +
            "        \"type\": \"text\",\n" +
            "        \"analyzer\": \"text_anlyzer\",\n" +
            "        \"search_analyzer\": \"ik_smart\",\n" +
            "        \"copy_to\": \"all\"\n" +
            "      },\n" +
            "      \"address\":{\n" +
            "        \"type\": \"keyword\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"price\":{\n" +
            "        \"type\": \"integer\"\n" +
            "      },\n" +
            "      \"score\":{\n" +
            "        \"type\": \"integer\"\n" +
            "      },\n" +
            "      \"brand\":{\n" +
            "        \"type\": \"keyword\",\n" +
            "        \"copy_to\": \"all\"\n" +
            "      },\n" +
            "      \"city\":{\n" +
            "        \"type\": \"keyword\"\n" +
            "      },\n" +
            "      \"starName\":{\n" +
            "        \"type\": \"keyword\"\n" +
            "      },\n" +
            "      \"business\":{\n" +
            "        \"type\": \"keyword\",\n" +
            "        \"copy_to\": \"all\"\n" +
            "      },\n" +
            "      \"location\":{\n" +
            "        \"type\": \"geo_point\"\n" +
            "      },\n" +
            "      \"pic\":{\n" +
            "        \"type\": \"keyword\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"isAD\" : {\n" +
            "        \"type\" : \"boolean\"\n" +
            "      },\n" +
            "      \"all\":{\n" +
            "        \"type\": \"text\",\n" +
            "        \"analyzer\": \"text_anlyzer\",\n" +
            "        \"search_analyzer\": \"ik_smart\"\n" +
            "      },\n" +
            "      \"suggestion\":{\n" +
            "          \"type\": \"completion\",\n" +
            "          \"analyzer\": \"completion_analyzer\",\n" +
            "          \"search_analyzer\": \"ik_smart\"\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}\n";

    public static final String HOTEL_MAPPING_BAK = "{\n" +
            "  \"mappings\": {\n" +
            "    \"properties\": {\n" +
            "      \"id\": {\n" +
            "        \"type\": \"keyword\"\n" +
            "      },\n" +
            "      \"name\": {\n" +
            "        \"type\": \"text\",\n" +
            "        \"analyzer\": \"ik_max_word\",\n" +
            "        \"copy_to\": \"all\"\n" +
            "      },\n" +
            "      \"address\": {\n" +
            "        \"type\": \"keyword\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"price\": {\n" +
            "        \"type\": \"integer\"\n" +
            "      },\n" +
            "      \"score\": {\n" +
            "        \"type\": \"integer\"\n" +
            "      },\n" +
            "      \"brand\": {\n" +
            "        \"type\": \"keyword\",\n" +
            "        \"copy_to\": \"all\"\n" +
            "      },\n" +
            "      \"city\": {\n" +
            "        \"type\": \"keyword\"\n" +
            "      },\n" +
            "      \"starName\": {\n" +
            "        \"type\": \"keyword\"\n" +
            "      },\n" +
            "      \"business\": {\n" +
            "        \"type\": \"keyword\",\n" +
            "        \"copy_to\": \"all\"\n" +
            "      },\n" +
            "      \"pic\": {\n" +
            "        \"type\": \"keyword\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"location\": {\n" +
            "        \"type\": \"geo_point\"\n" +
            "      },\n" +
            "      \"all\": {\n" +
            "        \"type\": \"text\",\n" +
            "        \"analyzer\": \"ik_max_word\"\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}";
}
