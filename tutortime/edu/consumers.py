from .models import TutoringSessionRoom
from channels.db import database_sync_to_async
from channels.generic.websocket import AsyncWebsocketConsumer
import json


class ChatConsumer(AsyncWebsocketConsumer):

    async def connect(self):

        # Determine room name and chat channel group name
        self.room_name = self.scope['url_route']['kwargs']['room_name']
        self.room_group_name = 'chat_%s' % self.room_name

        # Join room group
        await self.channel_layer.group_add(
            self.room_group_name,
            self.channel_name
        )

        # Increment the number of active students for this room
        # in the database for future reference by views
        await self.incrementActive(self.room_name)

        # Access WebSocket connections
        await self.accept()

    async def disconnect(self, close_code):

        # Leave room group
        await self.channel_layer.group_discard(
            self.room_group_name,
            self.channel_name
        )

        # Decrement the number of active students for this room
        # in the database for future reference by views
        await self.decrementActive(self.room_name)

    # Receive message from WebSocket
    async def receive(self, text_data):
        text_data_json = json.loads(text_data)
        verb = text_data_json['verb']
        tutor = text_data_json['tutor']
        tutee = text_data_json['tutee']
        tutorID = text_data_json['tutorID']
        tuteeID = text_data_json['tuteeID']
        message = text_data_json['message']

        # Send message to room group
        await self.channel_layer.group_send(
            self.room_group_name,
            {
                'type': 'chat_message',
                'verb': verb,
                'tutor': tutor,
                'tutee': tutee,
                'tutorID': tutorID,
                'tuteeID': tuteeID,
                'message': message
            }
        )

    # Receive message from room group
    async def chat_message(self, event):
        verb = event['verb']
        tutor = event['tutor']
        tutee = event['tutee']
        tutorID = event['tutorID']
        tuteeID = event['tuteeID']
        message = event['message']

        # Send message to WebSocket
        await self.send(text_data=json.dumps({
            'verb': verb,
            'tutor': tutor,
            'tutee': tutee,
            'tutorID': tutorID,
            'tuteeID': tuteeID,
            'message': message
        }))

    # Increment active users for a room in the database
    @database_sync_to_async
    def incrementActive(self, room_name):
        if TutoringSessionRoom.objects.filter(room_name=room_name).exists():
            room = TutoringSessionRoom.objects.get(room_name=room_name)
            room.active += 1
            room.save()

    # Decrement active users for a room in the database
    @database_sync_to_async
    def decrementActive(self, room_name):
        if TutoringSessionRoom.objects.filter(room_name=room_name).exists():
            room = TutoringSessionRoom.objects.get(room_name=room_name)
            # If us disconnecting sets the active count to 0, then just delete this room
            if room.active == 1:
                room.delete()
            else:
                room.active -= 1
                room.save()
