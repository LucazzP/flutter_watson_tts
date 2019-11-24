import 'dart:async';

import 'package:flutter/services.dart';

class FlutterWatsonTts {
  static const MethodChannel _channel =
      const MethodChannel('flutter_watson_tts');

  Future init(String apiKey) async {
    await _channel.invokeMethod("init", {"apiKey": apiKey});
  }

  Future<String> speak(String text) async{
    if(text != null){
      final String message = await _channel.invokeMethod("speak", {"text" : text});
      return message;
    } return "Text null";
  }

  Future<String> preLoad(String text) async{
    if(text != null){
      final String message = await _channel.invokeMethod("preLoad", {"text" : text});
      return message;
    } return "Text null";
  }
}
