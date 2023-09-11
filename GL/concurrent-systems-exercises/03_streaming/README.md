# TP3

## Publish and consume

Producer:

```bash
kcat -b localhost:9092 -P -t my-topic

Salut je m'appelle Nico
Je suis trop beau 
Et je fais pas d'intro
```

Consumer group 1:

```bash
kcat -b localhost:9092 -C -G my-consumer-group -J my-topic

% Group my-consumer-group rebalanced (memberi
d rdkafka-7789e119-6428-4056-b59c-8a20ffe85038): assigned: my-topic [0]
% Reached end of topic my-topic [0] at offset 9
% Group my-consumer-group rebalanced (memberi
d rdkafka-7789e119-6428-4056-b59c-8a20ffe85038): revoked: my-topic [0]
% Group my-consumer-group rebalanced (memberi
d rdkafka-7789e119-6428-4056-b59c-8a20ffe85038): assigned:
```

Consumer group 2:

```bash
kcat -b localhost:9092 -C -G my-consumer-group -J my-topic

% Group my-consumer-group rebalanced (memberid rdkafka-665e1d02-4b1a-4f73-aa6a-a4b0044aec2c): assigned: my-topic [0]
% Reached end of topic my-topic [0] at offset 9
{"topic":"my-topic","partition":0,"offset":9,"tstype
":"create","ts":1671109905597,"key":null,"payload":"Salut je m'appelle Nico"}
% Reached end of topic my-topic [0] at offset 10
{"topic":"my-topic","partition":0,"offset":10,"tstyp
e":"create","ts":1671109910358,"key":null,"payload":"Je suis trop beau"}
% Reached end of topic my-topic [0] at offset 11
{"topic":"my-topic","partition":0,"offset":11,"tstyp
e":"create","ts":1671109915708,"key":null,"payload":"Et je fais pas d'intro"}
% Reached end of topic my-topic [0] at offset 12
```

Consumer other group:

```bash
kcat -b localhost:9092 -C -G my-new-consumer-group -J my-topic

% Group my-new-consumer-group rebalanced (memberid rdkafka-5f3eba22-ed7d-432c-b8b1-2447ed562138): assigned: my-topic [0]
% Reached end of topic my-topic [0] at offset 9
{"topic":"my-topic","partition":0,"offset":9,"tstype":"create","ts":1671109905597,"key":null,"payload":"Salut je m'appelle Nico"}
% Reached end of topic my-topic [0] at offset 10
{"topic":"my-topic","partition":0,"offset":10,"tstype":"create","ts":1671109910358,"key":null,"payload":"Je suis trop beau"}
% Reached end of topic my-topic [0] at offset 11
{"topic":"my-topic","partition":0,"offset":11,"tstype":"create","ts":1671109915708,"key":null,"payload":"Et je fais pas d'intro"}
% Reached end of topic my-topic [0] at offset 12
```

### Questions

- How are the message distributed against the consumer ? Why is that ?
  - With the algorithm sticky round-robin, the messages are distributed to the consumers but it didn't change at every message. For example, it's change every 5 messages sent.
- Try changing the consumer group name on one of the terminal. What is happening now ?
  - The two consumer get all messages
- Change back to having a unique consumer group. What needs to be changed, so we can distribute messages across all the consumers ? You can use the UI for this
  - We need to change the number of partitions to the number of consumers.

## Downtimes

I made an application that read all the message of the topic and compare the reception time and the timestamp. When the difference is greater than 30 minutes, it print the reception time and the delay.

```
2023-01-11 22:05:51,802 main INFO [n.c.g.k.r.s.DownTimeFinder:63] main | Records analyzed by this step: 500
[...]
main INFO [n.c.g.k.r.s.DownTimeFinder:63] main | Records analyzed by this step: 500
2023-01-11 22:06:01,166 main INFO [n.c.g.k.r.s.DownTimeFinder:59] lambda$main$0 | Breakdown detected at: 2022-10-01T18:57:52.835Z	delay of: PT35M36.904S
2023-01-11 22:06:01,167 main INFO [n.c.g.k.r.s.DownTimeFinder:59] lambda$main$0 | Breakdown detected at: 2022-10-01T19:04:00.263Z	delay of: PT41M47.039S
2023-01-11 22:06:01,168 main INFO [n.c.g.k.r.s.DownTimeFinder:59] lambda$main$0 | Breakdown detected at: 2022-10-01T18:33:20.339Z	delay of: PT1H13M3.708S
2023-01-11 22:06:01,176 main INFO [n.c.g.k.r.s.DownTimeFinder:63] main | Records analyzed by this step: 500
[...]
2023-01-11 22:06:01,237 main INFO [n.c.g.k.r.s.DownTimeFinder:63] main | Records analyzed by this step: 87
2023-01-11 22:06:01,385 main INFO [n.c.g.k.r.s.DownTimeFinder:59] lambda$main$0 | Breakdown detected at: 2022-10-01T20:09:29.023Z	delay of: PT1H39M24.693S
2023-01-11 22:06:01,401 main INFO [n.c.g.k.r.s.DownTimeFinder:63] main | Records analyzed by this step: 500
[...]
2023-01-11 22:06:03,517 main INFO [n.c.g.k.r.s.DownTimeFinder:63] main | Records analyzed by this step: 500
2023-01-11 22:06:03,518 main INFO [n.c.g.k.r.s.DownTimeFinder:59] lambda$main$0 | Breakdown detected at: 2022-10-01T18:55:37.948Z	delay of: PT42M14.001S
2023-01-11 22:06:03,518 main INFO [n.c.g.k.r.s.DownTimeFinder:59] lambda$main$0 | Breakdown detected at: 2022-10-01T20:46:36.973Z	delay of: PT1H29M37.999S
2023-01-11 22:06:03,519 main INFO [n.c.g.k.r.s.DownTimeFinder:59] lambda$main$0 | Breakdown detected at: 2022-10-01T21:06:39.900Z	delay of: PT1H12M49.564S
2023-01-11 22:06:03,519 main INFO [n.c.g.k.r.s.DownTimeFinder:59] lambda$main$0 | Breakdown detected at: 2022-10-01T21:08:32.039Z	delay of: PT1H11M10.636S
2023-01-11 22:06:03,529 main INFO [n.c.g.k.r.s.DownTimeFinder:63] main | Records analyzed by this step: 500
[...]
2023-01-11 22:06:03,948 main INFO [n.c.g.k.r.s.DownTimeFinder:63] main | Records analyzed by this step: 230
2023-01-11 22:06:04,463 main INFO [n.c.g.k.r.s.DownTimeFinder:59] lambda$main$0 | Breakdown detected at: 2022-10-01T19:02:05.311Z	delay of: PT31M57.533S
2023-01-11 22:06:04,463 main INFO [n.c.g.k.r.s.DownTimeFinder:59] lambda$main$0 | Breakdown detected at: 2022-10-01T18:59:19.803Z	delay of: PT36M18.044S
2023-01-11 22:06:04,464 main INFO [n.c.g.k.r.s.DownTimeFinder:59] lambda$main$0 | Breakdown detected at: 2022-10-01T19:00:19.340Z	delay of: PT35M25.682S
2023-01-11 22:06:04,465 main INFO [n.c.g.k.r.s.DownTimeFinder:59] lambda$main$0 | Breakdown detected at: 2022-10-01T19:44:33.770Z	delay of: PT2H5M39.163S
2023-01-11 22:06:04,465 main INFO [n.c.g.k.r.s.DownTimeFinder:59] lambda$main$0 | Breakdown detected at: 2022-10-01T20:13:34.268Z	delay of: PT1H59M56.865S
2023-01-11 22:06:04,465 main INFO [n.c.g.k.r.s.DownTimeFinder:59] lambda$main$0 | Breakdown detected at: 2022-10-01T20:54:25.101Z	delay of: PT1H22M23.554S
2023-01-11 22:06:04,465 main INFO [n.c.g.k.r.s.DownTimeFinder:59] lambda$main$0 | Breakdown detected at: 2022-10-01T20:56:49.627Z	delay of: PT1H20M12.873S
2023-01-11 22:06:04,465 main INFO [n.c.g.k.r.s.DownTimeFinder:59] lambda$main$0 | Breakdown detected at: 2022-10-01T21:04:38.809Z	delay of: PT1H12M59.164S
2023-01-11 22:06:04,466 main INFO [n.c.g.k.r.s.DownTimeFinder:59] lambda$main$0 | Breakdown detected at: 2022-10-01T20:58:18.574Z	delay of: PT1H20M20.071S
2023-01-11 22:06:04,469 main INFO [n.c.g.k.r.s.DownTimeFinder:63] main | Records analyzed by this step: 500
[...]
2023-01-11 22:06:04,486 main INFO [n.c.g.k.r.s.DownTimeFinder:63] main | Records analyzed by this step: 47
2023-01-11 22:06:04,883 main INFO [n.c.g.k.r.s.DownTimeFinder:59] lambda$main$0 | Breakdown detected at: 2022-10-01T20:44:12.393Z	delay of: PT1H31M53.56S
2023-01-11 22:06:04,884 main INFO [n.c.g.k.r.s.DownTimeFinder:59] lambda$main$0 | Breakdown detected at: 2022-10-01T20:48:05.106Z	delay of: PT1H29M36.11S
2023-01-11 22:06:04,884 main INFO [n.c.g.k.r.s.DownTimeFinder:59] lambda$main$0 | Breakdown detected at: 2022-10-01T21:03:01.012Z	delay of: PT1H16M5.588S
2023-01-11 22:06:04,888 main INFO [n.c.g.k.r.s.DownTimeFinder:63] main | Records analyzed by this step: 500
2023-01-11 22:06:04,892 main INFO [n.c.g.k.r.s.DownTimeFinder:59] lambda$main$0 | Breakdown detected at: 2022-10-01T18:57:04.145Z	delay of: PT37M29.336S
2023-01-11 22:06:04,893 main INFO [n.c.g.k.r.s.DownTimeFinder:59] lambda$main$0 | Breakdown detected at: 2022-10-01T20:59:43.764Z	delay of: PT1H15M19.315S
2023-01-11 22:06:04,897 main INFO [n.c.g.k.r.s.DownTimeFinder:63] main | Records analyzed by this step: 500
[...]
2023-01-11 22:06:05,422 main INFO [n.c.g.k.r.s.DownTimeFinder:63] main | Records analyzed by this step: 431
2023-01-11 22:06:05,879 main INFO [n.c.g.k.r.s.DownTimeFinder:59] lambda$main$0 | Breakdown detected at: 2022-10-06T10:56:59.272Z	delay of: PT31M36.248S
2023-01-11 22:06:05,879 main INFO [n.c.g.k.r.s.DownTimeFinder:59] lambda$main$0 | Breakdown detected at: 2022-10-06T10:39:15.748Z	delay of: PT1H7M41.804S
2023-01-11 22:06:05,883 main INFO [n.c.g.k.r.s.DownTimeFinder:63] main | Records analyzed by this step: 500
[...]
2023-01-11 22:06:11,899 main INFO [n.c.g.k.r.s.DownTimeFinder:63] main | Records analyzed by this step: 500
2023-01-11 22:06:11,903 main INFO [n.c.g.k.r.s.DownTimeFinder:59] lambda$main$0 | Breakdown detected at: 2022-10-12T16:21:31.786Z	delay of: PT168H0.088S
[...]
2023-01-11 22:06:44,869 main INFO [n.c.g.k.r.s.DownTimeFinder:63] main | Records analyzed by this step: 0

Process finished with exit code 0

```
If we don't care about the message of the 2022-10-12 with 168 hours of delay, we can see two breakdown in 2022-10-1 et 2022-10-6.

It's the first message affected by the first downtime: 2022-10-01T18:55:37.948Z.
And this one is the first message of the second downtime: 2022-10-06T10:39:15.748Z
