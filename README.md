# Flatten Ranges POC

This POC provides an example on how to use Guava library to flatten **positive numeric** intervals.

## Use Case
In this use case, once a range is added to the set, following intersecting ranges should be split into smaller ranges
and inserted within the available gaps in the set. 

If no gap exists to accommodate any value in the new range, then the range is totally skipped. 
No intersections are allowed in the final set.


## Testing
Just modify the Ranges in **FlattenRanges** class and analyze the results.


## Authors

* **Soufiane Berouel**
