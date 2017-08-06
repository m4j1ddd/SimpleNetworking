"""Custom topology example

Two directly connected switches plus a host for each switch:

   host --- switch --- switch --- host

Adding the 'topos' dict with a key/value pair to generate our newly defined
topology enables one to pass in '--topo=mytopo' from the command line.
"""

from mininet.topo import Topo

class MyTopo( Topo ):
    "Simple topology example."

    def __init__( self ):
        "Create custom topo."

        # Initialize topology
        Topo.__init__( self )

        # Add hosts and switches
        Host1 = self.addHost( 'h1' )
        Host2 = self.addHost( 'h2' )
        Host3 = self.addHost( 'h3' )
        Host4 = self.addHost( 'h4' )
        Host5 = self.addHost( 'h5' )
        Host6 = self.addHost( 'h6' )
        Host7 = self.addHost( 'h7' )
        Host8 = self.addHost( 'h8' )
        Switch = self.addSwitch( 's1' )

        # Add links
        self.addLink(Host1, Switch)
        self.addLink(Host2, Switch)
        self.addLink(Host3, Switch)
        self.addLink(Host4, Switch)
        self.addLink(Host5, Switch)
        self.addLink(Host6, Switch)
        self.addLink(Host7, Switch)
        self.addLink(Host8, Switch)

topos = { 'mytopo': ( lambda: MyTopo() ) }

