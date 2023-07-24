import 'package:flutter/material.dart';
import 'package:ui/controllers/dtos.dart';
import 'package:ui/controllers/principal.dart';
import 'package:ui/controllers/ribbit_client.dart';
import 'package:ui/widgets/post.dart';

class PostScreen extends StatefulWidget {
  final RibbitClient client;
  final Principal? principal;
  final String postId;

  const PostScreen({required this.client, required this.principal, required this.postId, Key? key}): super(key: key);

  @override
  State<PostScreen> createState() => _PostScreenState();
}

class _PostScreenState extends State<PostScreen> {
  UserDtoV1? _profile;
  PostDtoV1? _post;

  @override
  void initState() {
    super.initState();
    load();
  }

  Future load() async {
    final profile = widget.principal != null ? await widget.client.getProfile(widget.principal!) : null;
    final post = await widget.client.getPost(widget.postId);
    setState(() {
      _profile = profile;
      _post = post;
    });
  }

  @override
  Widget build(BuildContext context) {
    final post = _post == null
      ? const Icon(Icons.error)
      : Post(post: _post!);
    
    return Scaffold(
      appBar: AppBar(
        title: const Text('Ribbit'),
        // actions: [
        //   if (_profile != null) ElevatedButton.icon(
        //       onPressed: doPost,
        //       label: const Text('Post'),
        //       icon: const Icon(Icons.add)
        //   ),
        //   SubSelector(
        //     subs: subs,
        //     selected: subs.firstOrNull,
        //     select: loadPosts,
        //   ),
        //   ProfileButton(
        //       profile: profile,
        //       logout: doLogout,
        //       login: doLogin
        //   ),
        // ],
      ),
      body: post
    );
  }
}