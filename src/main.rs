use std::fs::OpenOptions;
use std::io::Read;
use std::io::Result;
use std::io::Write;
use std::fmt::Display;
use std::vec::Vec;
use std::ops::Range;

fn main() {

    // Read input from file
    let mut input = read_file("input.txt").expect("Failed to read input.");
    
    // If empty, fill with random numbers and update input String
    if input.len() == 0 {
        println!("Input is empty, filling file with random numbers...");
        input = "10\n9\n8\n7\n6\n5\n4\n3\n2\n1\n".to_string();
        write_file("input.txt", &input);
    }

    // Read numbers into vector
    let mut data: Vec<i32> = Vec::new();
    for line in input.lines() {
        data.push(line.trim().parse().expect("Error reading numbers from String."));
    }

    // Sort the numbers
    bubble_sort(&mut data);

    // Print the result
    print_vector(&data);
}

fn print_vector<T: Display>(v: &Vec<T>) {
    for item in v {
        println!("{}", item);
    }
}

fn read_file(filename: &str) -> Result<String> {
    
    // Open file
    let mut file = OpenOptions::new()
        .append(true)
        .read(true)
        .create(true)
        .open(filename)
        .expect("Failed to open file.");

    // Read file into buffer
    let mut buffer = String::new();
    file.read_to_string(&mut buffer)?;

    Ok(buffer)
}

fn write_file(filename: &str, data: &str) {
    
    // Open file in write mode
    let mut file = OpenOptions::new()
        .append(true)
        .open(filename)
        .expect("Failed to open file.");

    // Write data to file
    file.write_all(data.as_bytes()).expect("Failed to write to file.");
}

// TODO: Move this function into meso crate library
fn bubble_sort(data: &mut Vec<i32>) {
    let mut count = 0;
    let mut temp  = 0;
    for i in (Range { start: 0, end: data.len() }) {
        for j in (Range { start: i, end: data.len() }) {
            if ( data[i] > data[j] )
            {
                temp = data[i];
                data[i] = data[j];
                data[j] = temp;
            }
            count += 1;
        }
    }
    println!("Comparisons made: {}.", count);
}