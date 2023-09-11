from __future__ import annotations
from abc import ABC, abstractmethod


class CompGraph():

    def __init__(self, in_nodes: list[ValueNode], out_nodes: list[ValueNode]):
        self.in_nodes = in_nodes
        self.out_nodes = out_nodes
        self.forwarded = False
        self.validate_graph()

    def validate_graph(self):
        """Place here a set of checking mechanisms - we can think of many"""
        # all input nodes should be ValueNode nodes
        for node in self.in_nodes:
            if not isinstance(node, ValueNode):
                raise Exception("Input node of CompGraph is not a ValueNode")
        # all output nodes should be ValueNode nodes
        for node in self.out_nodes:
            if not isinstance(node, ValueNode):
                raise Exception("Output node of CompGraph is not a ValueNode")

    def forward(self, input_values: list[float]):
        if len(input_values) != len(self.in_nodes):
            raise Exception("Can't forward: number of input differs to number of input nodes")
        for i, in_node in enumerate(self.in_nodes):
            in_node.set_value(input_values[i])
            in_node.forward()
        self.forwarded = True

    def backward(self):
        if not self.forwarded:
            raise Exception("Can't backward, you need to call forward first")
        for node in self.out_nodes:
            node.backward(1.0)

    def reset_values(self):
        for node in self.in_nodes:
            node.reset_values()
        self.forwarded = False


class MetaNode(ABC):
    """
    Meta node holds fields that are common to all nodes
    """

    def __init__(self):
        self.parents: list[MetaNode] = []
        self.children: list[MetaNode] = []
        self.input_ready: bool = False

    def add_child(self, node: MetaNode):
        self.children.append(node)

    def add_parent(self, node: MetaNode):
        self.parents.append(node)

    def connect_to(self, node: MetaNode):
        self.children.append(node)
        node.parents.append(self)

    @abstractmethod
    def forward(self):
        pass

    @abstractmethod
    def set_value(self, v):
        pass

    @abstractmethod
    def reset_values(self):
        pass

    @abstractmethod
    def backward(self, grad_z):
        pass


class ValueNode(MetaNode):

    def __init__(self, v: float = None):
        super().__init__()
        self.v: float = None
        self.grad_v: float = None
        if v is not None:
            self.v = v
            self.input_ready = True

    def set_value(self, v):
        self.v = v
        self.input_ready = True

    def set_grad_value(self, grad_v):
        self.grad_v = grad_v

    def reset_values(self):
        self.v = None
        self.input_ready = False
        for node in self.children:
            node.reset_values()

    def forward(self):
        if self.v is None:
            raise Exception('Forward not possible as no value set in this ValueNode')
        for node in self.children:
            node.set_value(self.v)
            node.forward()

    def backward(self, grad_z):
        self.grad_v = grad_z
        for node in self.parents:
            node.backward(grad_z)


class MultiplyNode(MetaNode):

    def __init__(self, in_nodes: list[ValueNode], out_node: ValueNode):
        super().__init__()
        # build connections to ValueNode objects
        if len(in_nodes) > 2:
            raise Exception('MultiplyNode does not support connection to more than 2 ValueNode')
        in_nodes[0].connect_to(self)
        in_nodes[1].connect_to(self)
        self.connect_to(out_node)
        # initialize internal values - here 2 inputs
        self.x1: float = None
        self.x2: float = None

    def set_value(self, v: float):
        if self.x1 is None:
            self.x1 = v
        elif self.x2 is None:
            self.x2 = v
            self.input_ready = True
        else:
            raise Exception('This node accepts 2 inputs that are already filled')

    def reset_values(self):
        self.x1 = None
        self.x2 = None
        self.input_ready = False
        for node in self.children:
            node.reset_values()

    def forward(self):
        if self.input_ready:
            z = self.x1 * self.x2
            for node in self.children:
                node.set_value(z)
                node.forward()

    def backward(self, grad_z):
        grad_x1 = grad_z * self.x2
        grad_x2 = grad_z * self.x1
        self.parents[0].backward(grad_x1)
        self.parents[1].backward(grad_x2)


class AddNode(MetaNode):

    def __init__(self, in_nodes: list[ValueNode], out_node: ValueNode):
        super().__init__()
        # build connections to ValueNode objects
        for node in in_nodes:
            node.connect_to(self)
        self.connect_to(out_node)
        # initialize internal values - here the input is a list of values
        self.inputs: list[float] = []

    def set_value(self, v: float):
        self.inputs.append(v)
        if len(self.inputs) == len(self.parents):
            self.input_ready = True
        elif len(self.inputs) > len(self.parents):
            raise Exception('All inputs are already set')

    def reset_values(self):
        self.inputs = []
        self.input_ready = False
        for node in self.children:
            node.reset_values()

    def forward(self):
        if self.input_ready:
            s = sum(self.inputs)
            for node in self.children:
                node.set_value(s)
                node.forward()

    def backward(self, grad_z):
        grad_x = 1.0 * grad_z
        # this value is distributed back to all input values
        for node in self.parents:
            node.backward(grad_x)


class SquareNode(MetaNode):

    def __init__(self, in_node: ValueNode, out_node: ValueNode):
        super().__init__()
        # build connections to ValueNode objects
        in_node.connect_to(self)
        self.connect_to(out_node)
        # initialize internal values - here the input is single value
        self.x: float = None

    def set_value(self, v: float):
        self.x = v
        self.input_ready = True

    def reset_values(self):
        self.x = None
        self.input_ready = False
        for node in self.children:
            node.reset_values()

    def forward(self):
        if self.input_ready:
            z = self.x * self.x
            for node in self.children:
                node.set_value(z)
                node.forward()

    def backward(self, grad_z):
        grad_x = 2 * self.x * grad_z
        for node in self.parents:
            node.backward(grad_x)


class MSELossNode(MetaNode):

    def __init__(self, in_nodes: list[ValueNode], out_node: ValueNode):
        super().__init__()
        # build connections to ValueNode objects
        if len(in_nodes) > 2:
            raise Exception('MultiplyNode does not support connection to more than 2 ValueNode')
        in_nodes[0].connect_to(self)
        in_nodes[1].connect_to(self)
        self.connect_to(out_node)
        # initialize internal values - here 2 inputs
        self.x1: float = None
        self.x2: float = None

    def set_value(self, v: float):
        if self.x1 is None:
            self.x1 = v
        elif self.x2 is None:
            self.x2 = v
            self.input_ready = True
        else:
            raise Exception('This node accepts 2 inputs that are already filled')

    def reset_values(self):
        self.x1 = None
        self.x2 = None
        self.input_ready = False
        for node in self.children:
            node.reset_values()

    def forward(self):
        if self.input_ready:
            z = (self.x1 - self.x2) ** 2 / 2
            for node in self.children:
                node.set_value(z)
                node.forward()

    def backward(self, grad_z):
        grad_x1 = grad_z * (self.x1 - self.x2)
        grad_x2 = grad_z * (self.x2 - self.x1)
        self.parents[0].backward(grad_x1)
        self.parents[1].backward(grad_x2)