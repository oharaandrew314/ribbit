class UserDtoV1 {
  final String id;
  final String name;

  UserDtoV1({required this.id, required this.name});

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

  SubDtoV1({required this.id, required this.name});

  @override
  String toString() => '/r/$id';
}

class PostDtoV1 {
  final String id;
  final String subId;
  final String title;
  final String content;
  final DateTime created;
  final DateTime? updated;

  PostDtoV1({
    required this.id,
    required this.subId,
    required this.title,
    required this.content,
    required this.created,
    required this.updated
  });
}

UserDtoV1 parseUser(Map<String, dynamic> json) {
  return UserDtoV1(
      id: json['id'],
      name: json['name']
  );
}

CursorDtoV1<SubDtoV1> parseSubs(Map<String, dynamic> json) {
  return CursorDtoV1(
      items: json['items'],
      next: json['next']
  );
}

SubDtoV1 parseSub(Map<String, dynamic> json) {
  return SubDtoV1(
      id: json['id'],
      name: json['name']
  );
}

CursorDtoV1<PostDtoV1> parsePosts(Map<String, dynamic> json) {
  return CursorDtoV1(
      items: json['items'],
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
      created: DateTime.parse(json['created']),
      updated: updated == null ? null : DateTime.parse(updated)
  );
}