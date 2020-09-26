**Interview exercise**

You are working for Crypto Inc. and we would like you to create a program to show the top 10 BUY or
SELL orders in the Crypto Inc. marketplace.
The Crypto Inc. 'Live Order Board' should support these features:
1) Placing an order. An order can be either a BUY or a SELL and should capture
- user id
- coin type (Litecoin, Ethereum.. etc)
- order quantity (how many coins)
- price per coin (e.g.: £125)
2) Cancel a registered order - this will remove the order from 'Live Order Board'
3) Get summary information of live orders (see explanation below)
Imagine we have received the following orders:
- a) SELL: 350.1 Ethereum @ £13.6 [user1]
- b) SELL: 50.5 for £14 [user2]
- c) SELL: 441.8 for £13.9 [user3]
- d) SELL: 3.5 for £13.6 [user4]
Our ‘Live Order Board’ should provide us the following summary information:
- 353.6 for £13.6 // order a + order d
- 441.8 for £13.9 // order c
- 50.5 for £14 // order b
The first thing to note here is that orders for the same price should be merged together (even when they
are from different users). In this case it can be seen that order a) and d) were for the same price (£13.6)
and this is why only their sum (353.6) is displayed (for £13.6) and not the individual orders (350.1 and
3.5).The last thing to note is that for SELL orders the orders with lowest prices are displayed first.
Opposite is true for the BUY orders.

Please provide the implementation of the live order board which will be packaged and shipped as a library
to be used by the UI team. No database or UI/WEB is needed for this assignment (we're absolutely fine
with in memory solution). The only important thing is that you just write it according to your normal
standards.
NOTE: if during your implementation you'll find that something could be designed in multiple different
ways, just implement the one which seems most reasonable to you and if you could provide a short (once
sentence) reasoning why you choose this way and not another one, it would be great


**Consideration/Assumptions**

The solution assumes that we are not dealing with tons of data (millions of orders) hence, we can calculate the 
summary of buying/selling orders on the fly.
An improved solution can be updating the buying/selling summary whenever an order gets booked or canceled.
