{
    "multipart": [
        {   "apply": { "model": "${fence_post}" }},
        {   "when": { "north": "true" },
            "apply": { "model": "${fence_side}", "uvlock": true }
        },
        {   "when": { "east": "true" },
            "apply": { "model": "${fence_side}", "y": 90, "uvlock": true }
        },
        {   "when": { "south": "true" },
            "apply": { "model": "${fence_side}", "y": 180, "uvlock": true }
        },
        {   "when": { "west": "true" },
            "apply": { "model": "${fence_side}", "y": 270, "uvlock": true }
        }
    ]
}