import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:flutter_watson_tts/flutter_watson_tts.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {

  FlutterWatsonTts tts = FlutterWatsonTts();

  @override
  void initState() {
    //! APIKEY IAM OPTIONS IBM CLOUD
    tts.init("");
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: Column(
            children: <Widget>[
              RaisedButton(
                onPressed: () async {
                  print(await tts.speak("Olá Fernanda"));
                },
                child: Text("Speak"),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
