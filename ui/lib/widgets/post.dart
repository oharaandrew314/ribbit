import 'package:flutter/widgets.dart';
import 'package:ui/controllers/dtos.dart';
import 'package:ui/widgets/sub_link.dart';
import 'package:ui/widgets/user_link.dart';

class Post extends StatelessWidget {
  final PostDtoV1 post;

  const Post({required this.post, final Key? key}): super(key: key);

  @override
  Widget build(BuildContext context) {
    final header = Row(
      children: [
        subLink(post.subId),
        const Text('Posted by'),
        userLink(post.authorName),
        Text(post.created.fromNow())
      ]
    );

    return Column(
      children: [
        header,
        Text(post.title),
        Text(post.content)
      ],
    );
  }
}