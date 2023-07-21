import 'dart:convert';
import 'dart:math';

import 'package:dart_jsonwebtoken/dart_jsonwebtoken.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter_web_auth/flutter_web_auth.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:ui/controllers/principal.dart';

class OAuthLoginProvider {
  final String _domain;
  final String _clientId;
  final SharedPreferences _prefs;
  final Random _random = Random.secure();

  OAuthLoginProvider(this._domain, this._clientId, this._prefs);

  Principal? getUser() {
     final idToken = _prefs.getString("idToken");
     final subject = _prefs.getString("subject");

     if (idToken == null || subject == null) return null;

     return Principal(
       subject: subject,
       idToken: idToken
     );
  }

  Future<void> logout() async {
    await _prefs.remove("idToken");
    await _prefs.remove("subject");
  }

  String _getRandString(int len) {
    var values = List<int>.generate(len, (i) =>  _random.nextInt(255));
    return base64UrlEncode(values);
  }

  Future<Principal> login() async {
    final redirectUri = kIsWeb ? Uri.base.resolve("auth.html") : Uri.parse("ribbit://auth");
    final uri = Uri.https(_domain, 'authorize', {
          'response_type': 'id_token',
          'client_id': _clientId,
          'redirect_uri': redirectUri.toString(),
          'scope': 'openid email',
          'prompt': 'login',
          'nonce': _getRandString(16)
        }
    );

    final result = Uri.parse(await FlutterWebAuth.authenticate(url: uri.toString(), callbackUrlScheme: redirectUri.scheme));
    final idToken = result.fragment.replaceAll("id_token=", "");
    final jwt = JWT.decode(idToken);

    final principal = Principal(
      subject: jwt.payload['sub'],
      idToken: idToken
    );

    await _prefs.setString("idToken", principal.idToken);
    await _prefs.setString("subject", principal.subject);

    return principal;
  }
}