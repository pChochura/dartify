package com.pointlessapps.dartify.errors.game.x01.move

class NotExistentPlayerException(val id: Long) : Exception("Player with id = $id does not exist")
