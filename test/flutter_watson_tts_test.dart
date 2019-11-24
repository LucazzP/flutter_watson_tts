import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:flutter_watson_tts/flutter_watson_tts.dart';

void main() {
  const MethodChannel channel = MethodChannel('flutter_watson_tts');

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await FlutterWatsonTts.platformVersion, '42');
  });
}
