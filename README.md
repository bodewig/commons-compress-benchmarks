# Benchmarks for Apache Commons Compress

Some JMH based benchmarks for my experiments
with [Apache Commons Compress](https://commons.apache.org/compress).

# Running Benchmarks

```
mvn package
java -jar target/benchmarks.jar
```

runs all benchmarks. For options run

```
java -jar target/benchmarks.jar -h
```

or see http://openjdk.java.net/projects/code-tools/jmh/

## Running Compression Benchmarks

`CompressionBenchmark` runs two tests for a bigger and a smaller
paykoad respectively. The parameter `format` selects the compression
algorithm, the default depends on what I'm currently looking in
to. You can specify a list of formats.

In order to compare throughput on the smaller payload for all formats
supported by Compress 1.14 run

```
java -jar target/benchmarks.jar CompressionBenchmark.compressSmallFile -pformat=bzip2,gz,xz,lzma,snappy-framed,deflate,lz4-framed
```
