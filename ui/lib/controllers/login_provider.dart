import 'dart:convert';
import 'dart:math';

import 'package:dart_jsonwebtoken/dart_jsonwebtoken.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter_web_auth/flutter_web_auth.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:ui/controllers/principal.dart';

class OAuthLoginProvider {
  final String domain;
  final String clientId;
  final SharedPreferences prefs;
  final Random random = Random.secure();

  OAuthLoginProvider(this.domain, this.clientId, this.prefs);

  Principal? getUser() {
     final idToken = prefs.getString("idToken");
     final subject = prefs.getString("subject");

     if (idToken == null || subject == null) return null;

     return Principal(
       subject: subject,
       idToken: idToken
     );
  }

  Future<void> logout() async {
    await prefs.remove("idToken");
    await prefs.remove("subject");
  }

  String _getRandString(int len) {
    var values = List<int>.generate(len, (i) =>  random.nextInt(255));
    return base64UrlEncode(values);
  }

  Future<Principal> login() async {
    final redirectUri = kIsWeb ? Uri.base.resolve("auth.html") : Uri.parse("ribbit://auth");
    final uri = Uri.https(domain, 'authorize', {
          'response_type': 'id_token',
          'client_id': clientId,
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

    await prefs.setString("idToken", principal.idToken);
    await prefs.setString("subject", principal.subject);

    return principal;
  }
}