class Message():
    def __init__(self, type: str, id: int, file: int, line: int) -> None:
        self.type = type
        self.id = id
        self.file = file
        self.line = line

    def info(self):
        return self.type, self.id, self.file, self.line
