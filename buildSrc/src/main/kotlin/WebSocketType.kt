enum class WebSocketType(val text: Boolean) {
  JSON(text = true),
  PROTOBUF(text = false),
  CBOR(text = false)
}