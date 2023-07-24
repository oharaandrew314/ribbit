import 'dart:convert';
import 'dart:io';

import 'package:ui/controllers/dtos.dart';
import 'package:http/http.dart' as http;
import 'package:ui/controllers/principal.dart';

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

  Future<CursorDtoV1<PostDtoV1>> listPosts(String subId) async {
    final resp = await http.get(
      host.resolve("/subs/$subId/posts"),
        // headers: { 'Authorization': 'Bearer $token' }
    );

    if (resp.statusCode != 200) throw HttpException("${resp.statusCode}: ${resp.body}");

    final json = jsonDecode(resp.body);
    return parsePosts(json);
  }

  Future<PostDtoV1?> getPost(String postId) async {
    final resp = await http.get(
      host.resolve('/posts/$postId')
    );

    if (resp.statusCode == 404) return null;
    if (resp.statusCode != 200) throw HttpException("${resp.statusCode}: ${resp.body}");

    final json = jsonDecode(resp.body);
    return parsePost(json);
  }

  Future<UserDtoV1?> createProfile({required Principal principal, required String name}) async {
    final resp = await http.post(
      host.resolve("/users"),
      headers: { 'Authorization': 'Bearer ${principal.idToken}' },
      body: jsonEncode({
        'name': name
      })
    );

    if (resp.statusCode == 409) return null;
    if (resp.statusCode != 200) throw HttpException("${resp.statusCode}: ${resp.body}");

    final json = jsonDecode(resp.body);
    return parseUser(json);
  }

  Future<UserDtoV1?> getProfile(Principal principal) async {
    final resp = await http.get(
        host.resolve("/users"),
        headers: { 'Authorization': 'Bearer ${principal.idToken}' }
    );

    if (resp.statusCode == 404) return null;
    if (resp.statusCode != 200) throw HttpException("${resp.statusCode}: ${resp.body}");

    final json = jsonDecode(resp.body);
    return parseUser(json);
  }

  Future<PostDtoV1?> createPost({
    required Principal principal,
    required String subId,
    required String title,
    required String content
  }) async {
    final resp = await http.post(
      host.resolve('/subs/$subId/posts'),
      headers: { 'Authorization': 'Bearer ${principal.idToken}' },
      body: jsonEncode({
        'title': title,
        'content': content
      })
    );

    if (resp.statusCode == 400) return null;
    if (resp.statusCode != 200) throw HttpException("${resp.statusCode}: ${resp.body}");

    final json = jsonDecode(resp.body);
    return parsePost(json);
  }
}