import 'dart:convert';
import 'dart:io';

import 'package:ui/controllers/dtos.dart';
import 'package:http/http.dart' as http;

class RibbitClient {
  final Uri host;

  RibbitClient(this.host);

  Future<CursorDtoV1<SubDtoV1>> listSubs() async {
    final resp = await http.get(
        host.resolve('/subs'),
        // headers: { 'Authorization': 'Bearer $token' }
    );

    if (resp.statusCode != 200) throw HttpException("${resp.statusCode}: ${resp.body}");

    final json = jsonDecode(resp.body);
    return parseSubs(json);
  }

  Future<CursorDtoV1<PostDtoV1>?> listPosts(String subId) async {
    final resp = await http.get(
      host.resolve("/subs/$subId/posts"),
        // headers: { 'Authorization': 'Bearer $token' }
    );

    if (resp.statusCode == 404) return null;
    if (resp.statusCode != 200) throw HttpException("${resp.statusCode}: ${resp.body}");

    final json = jsonDecode(resp.body);
    return parsePosts(json);
  }
}