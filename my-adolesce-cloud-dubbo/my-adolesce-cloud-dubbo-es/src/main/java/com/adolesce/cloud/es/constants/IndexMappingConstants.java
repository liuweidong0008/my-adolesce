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

    public static final String HOTEL_MAPPING = "{\n" +
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
