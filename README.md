## How to check everything:

- To test locally, you need to start docker-compose for Kafka first:
```
docker-compose -f docker-kafka.yml -p kafka up -d   
```
- to test everything running as a set of docker containers run this command:
```
docker-compose -f docker-full.yml -p slideshow up -d
```

## Request examples for REST:

### Add image

``` 
curl -X POST "http://localhost:8012/images/addImage" -H "Content-Type: application/json" -d '{"url":"https://media.istockphoto.com/id/2167996762/uk/%D1%84%D0%BE%D1%82%D0%BE/flamingos-on-lake.jpg?s=2048x2048&w=is&k=20&c=A_twH4lS0xkDGqh1oSO9aOEREPNEJpdRUkwOwsXN7nE=", "duration":"2"}'
```

**Response**

    {
        "id": 5,
        "url": "https://media.istockphoto.com/id/1445233447/photo/landscape-gardener-laying-turf-for-new-lawn.jpg?s=612x612&w=0&k=20&c=OAK9mHqL-5K36q85Fn46HTQZZUwN5yCR0GS80bu1iOI=",
        "duration": 5,
        "slideshows": []
    }

### Get image
```  
curl -X GET "http://localhost:8012/images/getImage/2"
``` 

**Response**

    {
        "id": 2,
        "url": "https://media.istockphoto.com/id/1312760160/photo/big-garden-grass-field-mowing-by-caucasian-gardener.jpg?s=612x612&w=0&k=20&c=cN19B3V2XHgqVbVBn_hlukI5U8Jhvy5ttNzcUma13Z4=",
        "duration": 10,
        "slideshows": [
            {
                "id": 1,
                "name": "My garden"
            }
        ]
    }



### Add slideshow with array of images' ids
```  
curl -X POST "http://localhost:8012/slideShow/addSlideshow" -H "Content-Type: application/json" -d '{"name":"My garden", "imagesIds":[2, 3]}'
``` 

**Response** 

    {
        "id": 27,
        "name": "My garden",
        "images": [
            {
                "id": 2,
                "url": "https://media.istockphoto.com/id/1458782106/photo/scenic-aerial-view-of-the-mountain-landscape-with-a-forest-and-the-crystal-blue-river-in.jpg?s=612x612&w=0&k=20&c=NXQ_OK6JtmyRRBef8Wd67UZ3scQJKySkXl1ORaActH4=",
                "duration": 5,
                "addedDate": "2024-12-17T19:56:05.61442"
            },
            {
                "id": 3,
                "url": "https://media.istockphoto.com/id/1418783006/photo/modern-backyard-outdoor-led-lighting-systems.jpg?s=612x612&w=0&k=20&c=W1ROEsfNT_uNdddqJqYFHpMmpDdr2nkZWEnKjnccC_U=",
                "duration": 3,
                "addedDate": "2024-12-17T19:56:05.61442"
            }
        ]
    }

### Get proof-of-play

``` 
curl -X GET "localhost:8012/slideShow/1/proof-of-play/1"
``` 

**Response**

    {
        "id": 3,
        "slideshowId": 1,
        "imageId": 1,
        "playedAt": "2024-12-17T14:47:38.813784"
    }

### Kafka messages

```
I have no name!@47291018f51a:/$ kafka-console-consumer.sh --bootstrap-server localhost:9092 --from-beginning --topic t-add-delete-image --property print.key=true
17      The image with 17 has been deleted
18      The image with 18 has been deleted
19      The next image ImageDto[id=19, url=https://media.istockphoto.com/id/1467597986/photo/professionally-landscaped-garden-flower-bed.jpg?s=612x612&w=0&k=20&c=PFgAjLdRomLMOLu47SPUcdTnHb8o89fGPaBFhocQmSg=, duration=5, slideshows=[]] has been created
19      The image with 19 has been deleted

```