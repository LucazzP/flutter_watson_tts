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

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: MainPage(),
    );
  }
}

class MainPage extends StatefulWidget {
  @override
  _MainPageState createState() => _MainPageState();
}

class _MainPageState extends State<MainPage> {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Plugin example app'),
      ),
      body: Center(
        child: Column(
          children: <Widget>[
            RaisedButton(
              onPressed: () async {
                Navigator.of(context).push(MaterialPageRoute(builder: (context) => AnotherPage()));
              },
              child: Text("Speak"),
            ),
          ],
        ),
      ),
    );
  }
}


class AnotherPage extends StatefulWidget {
  @override
  _AnotherPageState createState() => _AnotherPageState();
}

class _AnotherPageState extends State<AnotherPage> {

  FlutterWatsonTts tts = FlutterWatsonTts();

  @override
  void initState() {
    //! APIKEY IAM OPTIONS IBM CLOUD
    tts.doOnReady = () => print("TO PRONTO");
    tts.init("");
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(

      ),
      body: Center(
        child: Column(
          children: <Widget>[
            RaisedButton(
              onPressed: () async {
                print(await tts.speak("Olá, tudo bem? Eu tenho uma novidade para apresentar pra você. Poderia digitar o seu CPF, por favor?"));
              },
              child: Text("Speak"),
            ),
            RaisedButton(
              onPressed: () async {
                print(await tts.dispose());
              },
              child: Text("Dispose"),
            ),
          ],
        ),
      ),
    );
  }
}

