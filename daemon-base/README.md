Steps to create test chain (typical):

1. Open two daemon instances and connect them. Generate new addreses (A + B)
2. Mine 5 blocks to address A
3. Mine 5 blocks to address B
4. Mine first 10 blocks to maturity. Usually this means mining 20 blocks in total
5. Verify that each address has an equal balance
6. A sends $29.95 _equivalent_ to B (Multiple vouts)
7. B sends $10.00 _equivalent_ to A (Multiple vouts)
8. A sends `blocksize * 1.5` to B (Multiple vouts + multiple vins)
9. Mine another block to package above transactions