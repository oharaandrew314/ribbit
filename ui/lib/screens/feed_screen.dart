import 'package:flutter/widgets.dart';
import 'package:ui/controllers/ribbit_client.dart';

class FeedScreen extends StatefulWidget {
  final RibbitClient client;

  const FeedScreen({required this.client, final Key? key}) : super(key: key);

  @override
  State<StatefulWidget> createState() => _FeedScreenState();
}

class _FeedScreenState extends State<FeedScreen> {

  @override
  void initState() {
    // TODO: implement initState
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    // TODO: implement build
    throw UnimplementedError();
  }

}