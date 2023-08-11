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

resource "aws_dynamodb_table_item" "music_item" {
  table_name = aws_dynamodb_table.music_table.name
  hash_key   = aws_dynamodb_table.music_table.hash_key
  range_key  = aws_dynamodb_table.music_table.range_key
  item       = <<ITEM
{
  "SongTitle": {"S": "These Eyes"},
  "Artist": {"S": "The Guess Who"},
  "WrittenBy": {"L": [{"S": "Burton Cummings"}, {"S": "Randy Bachman"}]},
  "ProducedBy": {"L": [{"S": "Nimbus 9"}, {"S": "Jack Richardson"}]},
  "Album": {"S": "Wheatfield Soul"},
  "ReleasedIn": {"N": "1969"}
}
ITEM
}