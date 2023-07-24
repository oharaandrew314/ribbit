import 'package:jiffy/jiffy.dart';

class UserDtoV1 {
  final String name;

  UserDtoV1({required this.name});

  @override
  String toString() => '/u/$name';
}

class CursorDtoV1<Item> {
  final List<Item> items;
  final String? next;

  CursorDtoV1({
    required this.items,
    required this.next
  });
}

class SubDtoV1 {
  final String id;
  final String name;
  final String owner;

  SubDtoV1({required this.id, required this.name, required this.owner});

  @override
  String toString() => '/r/$id';
}

class PostDtoV1 {
  final String id;
  final String subId;
  final String title;
  final String content;
  final Jiffy created;
  final Jiffy? updated;
  final String authorName;

  PostDtoV1({
    required this.id,
    required this.subId,
    required this.title,
    required this.content,
    required this.created,
    required this.updated,
    required this.authorName
  });
}

UserDtoV1 parseUser(Map<String, dynamic> json) {
  return UserDtoV1(
      name: json['name']
  );
}

CursorDtoV1<SubDtoV1> parseSubs(Map<String, dynamic> json) {
  return CursorDtoV1(
      items: (json['items'] as List).map((i) => parseSub(i)).toList(),
      next: json['next']
  );
}

SubDtoV1 parseSub(Map<String, dynamic> json) {
  return SubDtoV1(
      id: json['id'],
      name: json['name'],
      owner: json['owner']
  );
}

CursorDtoV1<PostDtoV1> parsePosts(Map<String, dynamic> json) {
  return CursorDtoV1(
      items: (json['items'] as List).map((i) => parsePost(i)).toList(),
      next: json['next']
  );
}

PostDtoV1 parsePost(Map<String, dynamic> json) {
  final String? updated = json['updated'];

  return PostDtoV1(
      id: json['id'],
      subId: json['subId'],
      title: json['title'],
      content: json['content'],
      created: Jiffy.parse(json['created']),
      updated: updated == null ? null : Jiffy.parse(updated),
      authorName: json['authorName']
  );
}