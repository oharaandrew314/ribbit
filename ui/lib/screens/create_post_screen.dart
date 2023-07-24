import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:ui/controllers/login_provider.dart';
import 'package:ui/controllers/principal.dart';
import 'package:ui/controllers/ribbit_client.dart';
import 'package:ui/widgets/sub_selector.dart';

import '../controllers/dtos.dart';

class CreatePostScreen extends StatefulWidget {

  final RibbitClient client;
  final OAuthLoginProvider provider;

  const CreatePostScreen({required this.client, required this.provider, Key? key}): super(key: key);

  @override
  State<CreatePostScreen> createState() => _CreatePostScreenState();
}

class _CreatePostScreenState extends State<CreatePostScreen> {

  final _titleController = TextEditingController();
  final _contentController = TextEditingController();
  final _formKey = GlobalKey<FormState>();

  late Principal _principal;
  List<SubDtoV1> _subs = [];
  SubDtoV1? _sub;

  @override
  void initState() {
    super.initState();
    loadSubs();
    final principal = widget.provider.getUser();
    if (principal == null && mounted) {
      context.go('/');
    }

    _principal = principal!;
  }

  Future loadSubs() async {
    final page = await widget.client.listSubs();
    setState(() {
      _subs = page.items;
      _sub = page.items.firstOrNull;
    });
  }

  void selectSub(SubDtoV1 sub) {
    setState(() {
      _sub = sub;
    });
  }

  Future submit() async {
    final post = await widget.client.createPost(
        principal: _principal,
        subId: _sub!.id,
        title: _titleController.value.text,
        content: _contentController.value.text
    );

    if (post != null && mounted) {
      context.go('/posts/${post.id}');
    }
  }

  Future cancel() async {
    context.go('/');
  }

  @override
  Widget build(BuildContext context) {
    final subSelector = SubSelector(
        subs: _subs,
        selected: _subs.firstOrNull,
        select: selectSub
    );

    final titleField = TextFormField(
      controller: _titleController,
      decoration: const InputDecoration(
          hintText: 'Title'
      ),
      validator: (value) {
        if (value == null || value.isEmpty) {
          return 'Please enter some text';
        }
        return null;
      },
    );

    final contentField = TextFormField(
      controller: _contentController,
      keyboardType: TextInputType.multiline,
      maxLines: null,
      decoration: const InputDecoration(
        hintText: 'Content',
        hintMaxLines: 8
      ),
      validator: (value) {
        if (value == null || value.isEmpty) {
          return 'Please enter some text';
        }
        return null;
      },
    );

    final createButton = ElevatedButton(
      onPressed: submit,
      child: const Text('Post'),
    );

    final cancelButton = ElevatedButton(
        onPressed: cancel,
        child: const Text('Cancel')
    );

    final form = Form(
        key: _formKey,
        child: Column(
          children: [
            subSelector,
            titleField,
            contentField,
            Row(
              children: [cancelButton, createButton],
            )
          ],
        )
    );

    return Scaffold(
      appBar: AppBar(
        title: const Text('Create Post'),
      ),
      body: form,
    );
  }
}