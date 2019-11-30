import 'dart:async';

import 'package:flutter/services.dart';

class FlutterWatsonTts{
  static const MethodChannel _channel =
      const MethodChannel('flutter_watson_tts');

  FlutterWatsonTts(){
    _channel.setMethodCallHandler((call) async{
      if(call.method == 'doOnReady'){
        _doOnReady();
      }
    });
  }

  Function doOnReady;

  Future init(String apiKey) async {
    await _channel.invokeMethod("init", {"apiKey": apiKey});
  }

  Future<String> speak(String text) async{
    if(text != null){
      try {
        final String message = await _channel.invokeMethod("speak", {"text" : text});
        return message;
      } catch (e) {
        print(e);
        return e;
      }
    } return "Text null";
  }

  void _doOnReady(){
    if(doOnReady != null) doOnReady();
  }

  Future dispose() async {
    await _channel.invokeMethod('dispose');
  }
}
