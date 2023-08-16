resource "aws_dynamodb_table" "music_table" {
  name         = "tb_music"
  billing_mode = "PAY_PER_REQUEST"
  hash_key     = "SongTitle"
  range_key    = "Artist"

  attribute {
    name = "Artist"
    type = "S"
  }

  attribute {
    name = "SongTitle"
    type = "S"
  }
}

resource "aws_dynamodb_table_item" "music_item_1" {
  table_name = aws_dynamodb_table.music_table.name
  hash_key   = aws_dynamodb_table.music_table.hash_key
  range_key  = aws_dynamodb_table.music_table.range_key
  item       = <<ITEM
{
   "SongTitle":{
      "S":"These Eyes"
   },
   "Artist":{
      "S":"The Guess Who"
   },
   "WrittenBy":{
      "L":[
         {
            "S":"Burton Cummings"
         },
         {
            "S":"Randy Bachman"
         }
      ]
   },
   "ProducedBy":{
      "L":[
         {
            "S":"Nimbus 9"
         },
         {
            "S":"Jack Richardson"
         }
      ]
   },
   "Album":{
      "S":"Wheatfield Soul"
   },
   "ReleasedIn":{
      "N":"1969"
   }
}
ITEM
}

resource "aws_dynamodb_table_item" "music_item_2" {
  table_name = aws_dynamodb_table.music_table.name
  hash_key   = aws_dynamodb_table.music_table.hash_key
  range_key  = aws_dynamodb_table.music_table.range_key
  item       = <<ITEM
{
   "SongTitle":{
      "S":"Se..."
   },
   "Artist":{
      "S":"Djavan"
   },
   "Album":{
      "S":"Coisa de Acender"
   },
   "ProducedBy":{
      "L":[
         {
            "S":"Djavan"
         },
         {
            "S":"Ronnie Forster"
         }
      ]
   },
   "ReleasedIn":{
      "N":"1992"
   },
   "WrittenBy":{
      "L":[
         {
            "S":"Djavan"
         }
      ]
   }
}
ITEM
}

resource "aws_dynamodb_table_item" "music_item_3" {
  table_name = aws_dynamodb_table.music_table.name
  hash_key   = aws_dynamodb_table.music_table.hash_key
  range_key  = aws_dynamodb_table.music_table.range_key
  item       = <<ITEM
{
   "SongTitle":{
      "S":"Linha do Equador"
   },
   "Artist":{
      "S":"Djavan"
   },
   "Album":{
      "S":"Coisa de Acender"
   },
   "ProducedBy":{
      "L":[
         {
            "S":"Djavan"
         },
         {
            "S":"Ronnie Forster"
         }
      ]
   },
   "ReleasedIn":{
      "N":"1992"
   },
   "WrittenBy":{
      "L":[
         {
            "S":"Djavan"
         },
         {
            "S":"Caetano Veloso"
         }
      ]
   }
}
ITEM
}

resource "aws_dynamodb_table_item" "music_item_4" {
  table_name = aws_dynamodb_table.music_table.name
  hash_key   = aws_dynamodb_table.music_table.hash_key
  range_key  = aws_dynamodb_table.music_table.range_key
  item       = <<ITEM
{
   "SongTitle":{
      "S":"Linha do Equador"
   },
   "Artist":{
      "S":"Rael"
   },
   "Album":{
      "S":"Do Quintal"
   },
   "ProducedBy":{
      "L":[
         {
            "S":"Mônica Agena"
         }
      ]
   },
   "ReleasedIn":{
      "N":"2021"
   },
   "WrittenBy":{
      "L":[
         {
            "S":"Djavan"
         },
         {
            "S":"Caetano Veloso"
         }
      ]
   }
}
ITEM
}
